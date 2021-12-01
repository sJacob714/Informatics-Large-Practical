package uk.ac.ed.inf;

import com.google.gson.Gson;
import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Used to parse What3Words into a usable format
 */
public class What3WordsConverter {
    private static final HttpClient client = HttpClient.newHttpClient();
    private final String urlString;

    /**
     * Class Constructor for What3WordsConverter object.
     * Creates the initial part of the url string required to get What3Words information.
     *
     * @param machineName name of machine to connect to
     * @param port port where database is running
     */
    public What3WordsConverter(String machineName, String port){
        urlString = "http://" +machineName+ ":" +port+ "/words";
    }

    /**
     * Gets location information from What3Word.
     * Splits the word up and then accesses webserver to get location details
     * and then parses it in.
     *
     * @param words What3Word that we want to get location information about
     * @return usable Object of type What3Word that can be accessed to get required info about location
     */
    public What3Word convert(String words){
        HttpResponse<String> response = null;
        What3Word word = null;

        // splits word and inserts into url string that will be sent to server
        String[] splitWords = words.split("\\.");
        String urlString = this.urlString +"/"+ splitWords[0] +"/"+ splitWords[1] +"/"+ splitWords[2] +"/details.json";
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(urlString)).build();

        try {
            // Sends request command to server, stores response in response variable
            response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // catches any exceptions
        } catch (ConnectException e){
            System.out.println("Fatal error: Unable to connect to.");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Failed to access machine");
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.err.println("Failed to access machine ");
            e.printStackTrace();
        }

        //makes sure response isn't null
        if (response == null){
            System.err.println("Server Failure");
            System.exit(1);
        }
        // If status Code is 200, parses Json file, else returns error message
        if (response.statusCode()==200) {
            word = new Gson().fromJson(response.body(), What3Word.class);
        }
        else{
            System.err.println("Server Response Failure: "+response.statusCode());
            System.exit(1);
        }
        return word;
    }
}
