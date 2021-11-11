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
import java.util.PriorityQueue;

import static org.junit.Assert.*;

public class runCode {

    private static final String VERSION = "1.0.5";
    private static final String RELEASE_DATE = "September 28, 2021";

    private final LongLat appletonTower = new LongLat(-3.186874, 55.944494);
    private final LongLat businessSchool = new LongLat(-3.1873, 55.9430);
    private final LongLat greyfriarsKirkyard = new LongLat(-3.1928, 55.9469);

    @Test
    public void testRandomStuff(){
        System.out.println((new ArrayList<>()).size());
    }

    @Test
    public void checkLocations() {
        Locations location = new Locations("localhost", "9898");

        LongLat start = new LongLat(-3.1881, 	55.9448);
        LongLat end = new LongLat(-3.1880, 55.9446);
        assertFalse(location.noFly.outOfNoFlyCheck(start,end));

        start = new LongLat(-3.1881, 	55.9448);
        end = new LongLat(-3.1880, 	55.9445);
        assertFalse(location.noFly.outOfNoFlyCheck(start,end));

        start = new LongLat(-3.1889, 	55.9454);
        end = new LongLat(-3.1885, 	55.9455);
        assertTrue(location.noFly.outOfNoFlyCheck(start,end));

        start = new LongLat(-3.1892, 	55.9445);
        end = new LongLat(-3.1890, 	55.9445);
        assertTrue(location.noFly.outOfNoFlyCheck(start,end));

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

    @Test
    public void checkComparator(){
        LongLat end = new LongLat(-3.192473, 55.946233);

        LongLat x1 = new LongLat(-3.192472, 55.946233);
        LongLat x4 = new LongLat(-3.192475, 55.946233);
        LongLat x2 = new LongLat(-3.192477, 55.946233);
        LongLat x3 = new LongLat(-3.192476, 55.946233);
        LongLat x5 = new LongLat(-3.192470, 55.946233);
        LongLat x6 = new LongLat(-3.192474, 55.946234);
        LongLat x7 = new LongLat(-3.192473, 55.946234);

        PriorityQueue<LongLat> queue = new PriorityQueue<>(this::compare);
        queue.add(x3);
        queue.add(x2);
        queue.add(x1);
        queue.add(x4);
        queue.add(x5);
        queue.add(x6);
        queue.add(x7);

        for (int i =0; i<7; i++){
            LongLat item = queue.remove();
            System.out.println(item.lng + " " + item.lat);
            System.out.println(item.distanceTo(end));
        }
    }
    public int compare(LongLat x1, LongLat x2){
        LongLat end = new LongLat(-3.192473, 55.946233);
        return (int)((x1.distanceTo(end)-x2.distanceTo(end))*100000000);
    }

    @Test
    public void checkPathFinder(){
        LongLat start = new LongLat(-3.1914,55.9430);
        LongLat end1 = new LongLat(-3.1847,55.9431);
        LongLat end2 = new LongLat(-3.1847, 55.9461);
        Locations location = new Locations("localhost", "9898");
        ArrayList<LongLat> path;

        location.noFly.setNoFlyPerimeter(new ArrayList<>());
        PathFinder pathFinder = new PathFinder(location.noFly);

        path = pathFinder.findPath(start, end1);
        System.out.println(path.size());
        System.out.print("[");
        for (LongLat x: path){
            System.out.print("["+ x.lng +","+ x.lat +"]"+",");
        }
        System.out.println();

        path = pathFinder.findPath(start, end2);
        System.out.println(path.size());
        System.out.print("[");
        for (LongLat x: path){
            System.out.print("["+ x.lng +","+ x.lat +"]"+",");
        }
        System.out.println();

        location = new Locations("localhost", "9898");
        pathFinder = new PathFinder(location.noFly);

        path = pathFinder.findPath(start, end2);
        System.out.println(path.size());
        System.out.print("[");
        for (LongLat x: path){
            System.out.print("["+ x.lng +","+ x.lat +"]"+",");
        }
        System.out.println();

        LongLat start1 = new LongLat(-3.1898, 	55.9440);
        LongLat end3 = new LongLat(-3.1903, 55.9456);
        path = pathFinder.findPath(start1, end3);
        System.out.println(path.size());
        System.out.print("[");
        for (LongLat x: path){
            System.out.print("["+ x.lng +","+ x.lat +"]"+",");
        }
        System.out.println();

        LongLat start2 = new LongLat(-3.1895,55.9428);
        path = pathFinder.findPath(start2, end3);
        System.out.println(path.size());
        System.out.print("[");
        for (LongLat x: path){
            System.out.print("["+ x.lng +","+ x.lat +"]"+",");
        }
        System.out.println();
    }
}
