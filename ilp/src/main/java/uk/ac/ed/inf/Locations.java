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
    private FeatureCollection noFlyZone;
    private List<Line2D> noFlyPerimeter = new ArrayList<>();

    public Locations(String machineName, String port){
        String urlString;
        HttpRequest request;
        HttpResponse<String> response = null;

        urlString = "http://" +machineName+ ":" +port+ "/buildings/landmarks.geojson";
        request = HttpRequest.newBuilder().uri(URI.create(urlString)).build();

        try{
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (ConnectException e){
            System.out.println("Fatal error: Unable to connect to " +
                    machineName + " at port " + port + ".");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Failed to access machine " +machineName+ " at port " +port);
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.err.println("Failed to access machine " +machineName+ " at port " +port);
            e.printStackTrace();
        }

        if (response.statusCode()==200){
            landmarks = FeatureCollection.fromJson(response.body());
        }
        else{
            System.err.println("Server Response Failure: "+response.statusCode());
            System.exit(1);
        }

        urlString = "http://" +machineName+ ":" +port+ "/buildings/no-fly-zones.geojson";
        request = HttpRequest.newBuilder().uri(URI.create(urlString)).build();

        try{
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (ConnectException e){
            System.out.println("Fatal error: Unable to connect to " + machineName + " at port " + port + ".");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Failed to access machine " +machineName+ " at port " +port);
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.err.println("Failed to access machine " +machineName+ " at port " +port);
            e.printStackTrace();
        }

        if (response.statusCode()==200){
            noFlyZone = FeatureCollection.fromJson(response.body());

            for (Feature feature: noFlyZone.features()){
                for (List<Point> pointList: ((Polygon) feature.geometry()).coordinates()){
                    for (int i=0; i<pointList.size()-1; i++){;
                        noFlyPerimeter.add(new Line2D.Double(pointList.get(i).longitude(), pointList.get(i).latitude(), pointList.get(i + 1).longitude(), pointList.get(i + 1).latitude()));
                    }
                }
            }


        }

        else{
            System.err.println("Server Response Failure: "+response.statusCode());
            System.exit(1);
        }
    }

    public boolean outOfNoFlyCheck(LongLat start, LongLat end){
        Line2D line = new Line2D.Double(start.longitude, start.latitude, end.longitude, end.latitude);

        for (Line2D perimeter: noFlyPerimeter){
            if (line.intersectsLine(perimeter)){
                return false;
            }
        }

        return true;
    }
}
