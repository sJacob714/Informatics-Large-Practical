package uk.ac.ed.inf;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


public class Menus {
    private static final HttpClient client = HttpClient.newHttpClient();
    private String machineName;
    private String port;
    public ArrayList<Shop> ShopsList;

    /**
     * Constructor for the Menus class
     * Sends a get request to the server and parses the response into a list of Menu types
     *
     * @param machineName name of the machine that needs to be accessed
     * @param port port where web server is running
     */
    public Menus(String machineName, String port){
        this.machineName = machineName;
        this.port = port;

        String urlString = "http://" +machineName+ ":" +port+ "/menus/menus.json";
        HttpResponse<String> response = null;
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(urlString)).build();

        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Type listType = new TypeToken<ArrayList<Shop>>(){}.getType();
        ShopsList = new Gson().fromJson(response.body(), listType);
    }

    /**
     * Calculates cost of delivering a number of items
     *
     * @param items variable number of items that are to be delivered
     * @return cost of delivering the items
     */
    public int getDeliveryCost(String... items){
        int totalCost = 50;

        List<String> searchList = Arrays.asList(items);

        menuSearch:
        for (Shop shops : ShopsList){
            for (Shop.MenuItem item: shops.menu){
                if (searchList.contains(item.item)) {
                    totalCost = totalCost + item.pence;
                    searchList.remove(item);

                    if (searchList.isEmpty()) {
                        break menuSearch;
                    }
                }
            }
        }

        return totalCost;
    }
}
