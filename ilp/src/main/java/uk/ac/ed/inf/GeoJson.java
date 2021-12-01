package uk.ac.ed.inf;

import com.mapbox.geojson.*;

import java.io.FileWriter;
import java.io.IOException;

import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for methods involving GeoJson
 */
public class GeoJson {
    private static final HttpClient client = HttpClient.newHttpClient();
    private final String machineName;
    private final String port;

    /**
     * Constructor for GeoJson class.
     * Stores machine name and port
     *
     * @param machineName name of the machine that needs to be accessed
     * @param port port where web server is running
     */
    public GeoJson(String machineName, String port) {
        this.machineName = machineName;
        this.port = port;
    }

    /**
     * Gets noFlyZone from server and creates NoFlyZone object using response.
     *
     * @return NoFlyZone Object that was created using the response
     */
    public NoFlyZone getNoFlyZone() {
        NoFlyZone noFlyZone = null;
        String urlString;
        HttpRequest request;
        HttpResponse<String> response = null;

        //builds urlString and gets response from server
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
            System.err.println(" Failed to access machine " + machineName + " at port " + port);
            e.printStackTrace();
        }

        //makes sure response isn't null
        if (response == null){
            System.err.println("Server Failure");
            System.exit(1);
        }
        // If status code is 200, uses response to create NoFlyZone object
        if (response.statusCode() == 200) {
            noFlyZone = new NoFlyZone(response);
        }
        else {
            System.err.println("Server Response Failure: " + response.statusCode());
            System.exit(1);
        }
        return noFlyZone;
    }

    /**
     * Creates text file, parses flight path data into
     * Json format and writes to file.
     *
     * @param flightPath list of coordinates drone had travelled to
     * @param fileName name of file that needs to be created
     */
    public void writeToGeoJson(ArrayList<LongLat> flightPath, String fileName) {
        // turns flight path into list of Point objects
        List<Point> points = new ArrayList<>();
        for (int i = 0; i < flightPath.size() - 1; i++) {
            points.add(Point.fromLngLat(flightPath.get(i).lng, flightPath.get(i).lat));
        }
        //converts list of Point objects to Json string of feature collection
        Geometry geometry = (Geometry) LineString.fromLngLats(points);
        Feature feature = Feature.fromGeometry(geometry);
        FeatureCollection featureCollection = FeatureCollection.fromFeature(feature);
        String jsonString = featureCollection.toJson();

        try {
            // does not append to any existing file, instead overwrites
            FileWriter writer = new FileWriter(fileName, false);
            writer.write(jsonString);
            writer.close();
            System.out.println("Created and Written to " +fileName);
            System.out.println();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

