package uk.ac.ed.inf;

import com.mapbox.geojson.*;
import org.junit.Test;

import java.awt.geom.Line2D;
import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class runCode {

    private static final String VERSION = "1.0.5";
    private static final String RELEASE_DATE = "September 28, 2021";

    private final LongLat appletonTower = new LongLat(-3.186874, 55.944494);
    private final LongLat businessSchool = new LongLat(-3.1873, 55.9430);
    private final LongLat greyfriarsKirkyard = new LongLat(-3.1928, 55.9469);

    @Test
    public void checkLocations() {
        Locations location = new Locations("localhost", "9898");

        LongLat start = new LongLat(-3.1881, 	55.9448);
        LongLat end = new LongLat(-3.1880, 55.9446);
        assertFalse(location.outOfNoFlyCheck(start,end));

        start = new LongLat(-3.1881, 	55.9448);
        end = new LongLat(-3.1880, 	55.9445);
        assertFalse(location.outOfNoFlyCheck(start,end));

        start = new LongLat(-3.1889, 	55.9454);
        end = new LongLat(-3.1885, 	55.9455);
        assertTrue(location.outOfNoFlyCheck(start,end));

        start = new LongLat(-3.1892, 	55.9445);
        end = new LongLat(-3.1890, 	55.9445);
        assertTrue(location.outOfNoFlyCheck(start,end));

        start = new LongLat(-3.1889, 	55.9447);
        end = new LongLat(-3.1890, 	55.9448);
        //assertTrue(location.outOfNoFlyCheck(start,end));
    }

    @Test
    public void checkDatabase(){
        What3WordsConverter converter = new What3WordsConverter("localhost", "9898");
        String date = "2022-05-14";
        Database database = new Database("localhost", "9876", date, converter);
        for (int i = 0; i<database.orders.size(); i++){
            System.out.println(database.orders.get(i).orderNo +" "+ database.orders.get(i).deliveryDate +" "+ database.orders.get(i).customer +" "+ database.orders.get(i).deliverTo.square.northeast.lng +" "+ database.orders.get(i).deliverTo.square.northeast.lat);
            for (int j = 0; j<database.orders.get(i).orderItems.size(); j++){
                System.out.print(database.orders.get(i).orderItems.get(j)+"     ");
            }
            System.out.println();
        }
    }
}
