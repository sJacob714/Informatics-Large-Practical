package uk.ac.ed.inf;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;


public class Menus {
    private static final HttpClient client = HttpClient.newHttpClient();
    public ArrayList<Menu> menus;

    /**
     * Constructor for the Menus class
     * Sends a get request to the server and parses the response into a list of Menu types
     *
     * @param machineName name of the machine that needs to be accessed
     * @param port port where web server is running
     */
    public Menus(String machineName, String port){

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

        Type listType = new TypeToken<ArrayList<Menu>>(){}.getType();
        menus = new Gson().fromJson(response.body(), listType);
    }

    /**
     * Calculates cost of delivering a number of items
     *
     * @param items variable number of items that are to be delivered
     * @return cost of delivering the items
     */
    public int getDeliveryCost(String... items){
        int totalCost = 50;
        for (String search: items){

            menuSearch:
            for (Menu menu: menus){
                for (MenuItem item: menu.menu){
                    if (item.item.equals(search)){
                        totalCost = totalCost + item.pence;
                        break menuSearch;
                    }
                }
            }

        }
        return totalCost;
    }
}
