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

    public int getDeliveryCost(String... items){
        int totalCost = 50;
        for (String search: items){
            outerLoop:
            for (Menu menu: menus){
                for (MenuItem item: menu.menu){
                    if (item.item.equals(search)){
                        totalCost = totalCost + item.pence;
                        break outerLoop;
                    }
                }
            }
        }
        return totalCost;
    }
}
