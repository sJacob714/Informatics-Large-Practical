package uk.ac.ed.inf;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Menus {
    private static final HttpClient client = HttpClient.newHttpClient();

    //List of Shop details and menus
    private ArrayList<Shop> ShopsList;

    /**
     * Class constructor for the Menus class.
     * Gets information on all menu items form server.
     * Sends a get request to the server and parses the response into a list of Menu types.
     *
     * @param machineName name of the machine that needs to be accessed
     * @param port port where web server is running
     */
    public Menus(String machineName, String port){

        // Builds request command to send to server
        String urlString = "http://" +machineName+ ":" +port+ "/menus/menus.json";
        HttpResponse<String> response = null;
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(urlString)).build();

        try {
            // Sends request command to server, stores response in response variable
            response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // catches any exceptions
        } catch (ConnectException e){
            System.out.println("Fatal error: Unable to connect to " +
                    machineName + " at port " + port + ".");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Failed to access machine " +machineName+ " at port " +port);
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.err.println("Failed to access machine  " +machineName+ " at port " +port);
            e.printStackTrace();
        }

        //makes sure response isn't null
        if (response == null){
            System.err.println("Server Failure");
            System.exit(1);
        }
        // If status Code is 200, parses Json file, else returns error message
        if (response.statusCode()==200) {
            Type listType = new TypeToken<ArrayList<Shop>>() {
            }.getType();
            ShopsList = new Gson().fromJson(response.body(), listType);
        }
        else{
            System.err.println("Server Response Failure: "+response.statusCode());
            System.exit(1);
        }
    }

    /**
     * Calculates cost of delivering an order.
     * For every item requested, iterates through menus until item name is found
     * and then adds price onto total cost.
     *
     * @param items variable number of items that are to be delivered
     * @return cost of delivering the items
     */
    public int getDeliveryCost(String... items){
        // initial cost is 50 as standard delivery charge is 50p
        int totalCost = 50;

        // Goes through every item to be found
        for (String search: items) {
            // Iterates through every item in every menu
            menuSearch:
            for (Shop shops : ShopsList) {
                for (Shop.MenuItem item : shops.menu) {

                    // If item is within the searchList, add price to totalCost and break out of menuSearch
                    if (item.item.equals(search)) {
                        totalCost = totalCost + item.pence;
                        break menuSearch;
                    }
                }
            }
        }
        return totalCost;
    }

    /**
     * Gets coordinates of every shop that needs to be visited to get items.
     * For every item requested, iterates through menus until item name is found
     * and then adds shop coordinates to list of coordinates.
     *
     * @param converter Used to convert What3Word to usable formats
     * @param items variable number of items that are to be delivered
     * @return list of shop coordinates that need to be visited
     */
    public ArrayList<LongLat> getShopCoordinates(What3WordsConverter converter, String... items){
        ArrayList<LongLat> coordinates = new ArrayList<>();
        // used to stop shop coordinates from being added to list twice
        List<Shop> previousShops = new ArrayList<>();
        LongLat tempCoordinate;
        double centreLng;
        double centreLat;
        What3Word word;

        // Goes through every item to be found
        for (String search: items) {
            // Iterates through every item in every menu
            menuSearch:
            for (Shop shop : ShopsList) {
                for (Shop.MenuItem item : shop.menu) {

                    // If item is within the searchList, adds shop location to list
                    if (item.item.equals(search)) {
                        // makes sure shop isn't already in the list
                        if (previousShops.contains(shop)){
                            break menuSearch;
                        }
                        previousShops.add(shop);

                        // finds centre of What3Words square and adds to list of shop coordinates
                        word = converter.convert(shop.location);
                        centreLng = (word.square.northeast.lng + word.square.southwest.lng)/2;
                        centreLat = (word.square.northeast.lat + word.square.southwest.lat)/2;
                        tempCoordinate = new LongLat(centreLng, centreLat);

                        coordinates.add(tempCoordinate);
                        break menuSearch;
                    }
                }
            }
        }
        return coordinates;
    }
}
