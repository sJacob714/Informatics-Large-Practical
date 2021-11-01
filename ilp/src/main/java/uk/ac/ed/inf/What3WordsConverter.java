package uk.ac.ed.inf;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

public class What3WordsConverter {
    private static final HttpClient client = HttpClient.newHttpClient();
    private final String urlString;

    public What3WordsConverter(String machineName, String port){
        urlString = "http://" +machineName+ ":" +port+ "/words";
    }

    public What3Word convert(String words){
        String[] splitWords = words.split("\\.");
        HttpResponse<String> response = null;
        What3Word word = null;

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
            System.err.println("Failed to access machine");
            e.printStackTrace();
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
