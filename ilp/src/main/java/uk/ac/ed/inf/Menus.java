package uk.ac.ed.inf;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;

import com.google.gson.Gson;


public class Menus {
    private static final HttpClient client = HttpClient.newHttpClient();

    public Menus(String name, String port){

    }

    public int getDeliveryCost(String[] items){
        return 0;
    }
}
