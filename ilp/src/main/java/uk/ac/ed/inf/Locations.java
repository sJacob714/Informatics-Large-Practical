package uk.ac.ed.inf;

import com.mapbox.geojson.*;

import java.awt.geom.Line2D;

import java.io.IOException;

import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.util.ArrayList;
import java.util.List;

public class Locations {
    private static final HttpClient client = HttpClient.newHttpClient();
    private FeatureCollection landmarks;
    private NoFlyZone noFlyZone;
    public String machineName;
    public String port;

    public Locations(String machineName, String port) {
        this.machineName = machineName;
        this.port = port;
        //getLandmarks();
        //getNoFlyZone();
    }

    /**
     * Gets landmarks from server and parses it in
     *
     */
    public FeatureCollection getLandmarks() {
        String urlString;
        HttpRequest request;
        HttpResponse<String> response = null;

        urlString = "http://" + machineName + ":" + port + "/buildings/landmarks.geojson";
        request = HttpRequest.newBuilder().uri(URI.create(urlString)).build();

        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (ConnectException e) {
            System.out.println("Fatal error: Unable to connect to " +
                    machineName + " at port " + port + ".");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Failed to access machine " + machineName + " at port " + port);
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.err.println("Failed to access machine " + machineName + " at port " + port);
            e.printStackTrace();
        }

        if (response.statusCode() == 200) {
            landmarks = FeatureCollection.fromJson(response.body());
        } else {
            System.err.println("Server Response Failure: " + response.statusCode());
            System.exit(1);
        }
        return landmarks;
    }

    /**
     * Gets noFlyZone from server and parses it in
     * Saves this noFlyZone as a list of straight lines around the perimeter
     *
     */
    public NoFlyZone getNoFlyZone() {
        String urlString;
        HttpRequest request;
        HttpResponse<String> response = null;

        urlString = "http://" + machineName + ":" + port + "/buildings/no-fly-zones.geojson";
        request = HttpRequest.newBuilder().uri(URI.create(urlString)).build();

        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (ConnectException e) {
            System.out.println("Fatal error: Unable to connect to " + machineName + " at port " + port + ".");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Failed to access machine " + machineName + " at port " + port);
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.err.println("Failed to access machine " + machineName + " at port " + port);
            e.printStackTrace();
        }

        if (response.statusCode() == 200) {
            noFlyZone = new NoFlyZone(response);
        }
        else {
            System.err.println("Server Response Failure: " + response.statusCode());
            System.exit(1);
        }
        return noFlyZone;
    }




}

