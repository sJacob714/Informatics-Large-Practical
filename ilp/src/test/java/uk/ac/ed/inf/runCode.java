
package uk.ac.ed.inf;

import org.junit.Test;

import java.util.ArrayList;
import java.util.PriorityQueue;

import static org.junit.Assert.*;

public class runCode {
/**
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
        GeoJson location = new GeoJson("localhost", "9898");
        NoFlyZone noFlyZone = location.getNoFlyZone();

        LongLat start = new LongLat(-3.1881, 	55.9448);
        LongLat end = new LongLat(-3.1880, 55.9446);
        assertFalse(noFlyZone.staysOutOfNoFly(start,end));

        start = new LongLat(-3.1881, 	55.9448);
        end = new LongLat(-3.1880, 	55.9445);
        assertFalse(noFlyZone.staysOutOfNoFly(start,end));

        start = new LongLat(-3.1889, 	55.9454);
        end = new LongLat(-3.1885, 	55.9455);
        assertTrue(noFlyZone.staysOutOfNoFly(start,end));

        start = new LongLat(-3.1892, 	55.9445);
        end = new LongLat(-3.1890, 	55.9445);
        assertTrue(noFlyZone.staysOutOfNoFly(start,end));

        start = new LongLat(-3.1889, 	55.9447);
        end = new LongLat(-3.1890, 	55.9448);
        //assertTrue(location.outOfNoFlyCheck(start,end));
    }

    @Test
    public void checkDatabase(){
        What3WordsConverter converter = new What3WordsConverter("localhost", "9898");
        Menus menus = new Menus("localhost","9898");
        String date = "2022-05-14";
        Database database = new Database("localhost", "1527");
        ArrayList<Order> orders = database.getOrders(date, converter, menus);
        for (int i = 0; i<orders.size(); i++){
            //System.out.println(orders.get(i).orderNo +" "+ orders.get(i).deliveryDate +" "+ orders.get(i).customer +" "+ orders.get(i).deliverTo.lng +" "+ orders.get(i).deliverTo.lat +" "+ orders.get(i).deliveryCost);
            for (int j = 0; j<orders.get(i).orderItems.size(); j++){
                System.out.print(orders.get(i).orderItems.get(j)+"     ");
            }
            System.out.println();
            for (int j = 0; j<orders.get(i).shopCoordinates.size(); j++){
                System.out.print("(" + orders.get(i).shopCoordinates.get(j).lng+ ", "+orders.get(i).shopCoordinates.get(j).lat+")     ");
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
        LongLat end = new LongLat(-3.1847,55.9431);
        LongLat end2 = new LongLat(-3.1847, 55.9461);
        GeoJson location = new GeoJson("localhost", "9898");
        NoFlyZone noFlyZone = location.getNoFlyZone();
        ArrayList<LongLat> path;
        ArrayList<Integer> angleList;

        //noFlyZone.setNoFlyPerimeter(new ArrayList<>());
        PathFinder pathFinder = new PathFinder(noFlyZone);

        pathFinder.createPath(start, end);
        path=pathFinder.getPath();
        System.out.println(path.size());
        System.out.print("[");
        for (LongLat x: path){
            System.out.print("["+ x.lng +","+ x.lat +"]"+",");
        }
        System.out.println();
        angleList = pathFinder.getAngleList();
        System.out.println(angleList.size());
        for (int x: angleList){
            System.out.print(x+" ");
        }
        System.out.println();
        System.out.println();


        pathFinder.createPath(start, end2);
        path=pathFinder.getPath();
        System.out.println(path.size());
        System.out.print("[");
        for (LongLat x: path){
            System.out.print("["+ x.lng +","+ x.lat +"]"+",");
        }
        System.out.println();
        angleList = pathFinder.getAngleList();
        System.out.println(angleList.size());
        for (int x: angleList){
            System.out.print(x+" ");
        }
        System.out.println();
        System.out.println();


        location = new GeoJson("localhost", "9898");
        noFlyZone = location.getNoFlyZone();
        pathFinder = new PathFinder(noFlyZone);

        pathFinder.createPath(start, end2);
        path=pathFinder.getPath();
        System.out.println(path.size());
        System.out.print("[");
        for (LongLat x: path){
            System.out.print("["+ x.lng +","+ x.lat +"]"+",");
        }
        System.out.println();
        angleList = pathFinder.getAngleList();
        System.out.println(angleList.size());
        for (int x: angleList){
            System.out.print(x+" ");
        }
        System.out.println();
        System.out.println();


        LongLat start1 = new LongLat(-3.1898, 	55.9440);
        LongLat end3 = new LongLat(-3.1903, 55.9456);
        pathFinder.createPath(start1, end3);
        path=pathFinder.getPath();
        System.out.println(path.size());
        System.out.print("[");
        for (LongLat x: path){
            System.out.print("["+ x.lng +","+ x.lat +"]"+",");
        }
        System.out.println();
        angleList = pathFinder.getAngleList();
        System.out.println(angleList.size());
        for (int x: angleList){
            System.out.print(x+" ");
        }
        System.out.println();
        System.out.println();


        LongLat start2 = new LongLat(-3.1895,55.9428);
        pathFinder.createPath(start2, end3);
        path=pathFinder.getPath();
        System.out.println(path.size());
        System.out.print("[");
        for (LongLat x: path){
            System.out.print("["+ x.lng +","+ x.lat +"]"+",");
        }
        System.out.println();
        angleList = pathFinder.getAngleList();
        System.out.println(angleList.size());
        for (int x: angleList){
            System.out.print(x+" ");
        }
        for (int i = 0; i<path.size()-1; i++){
            System.out.println(path.get(i).distanceTo(path.get(i+1)));
        }
        System.out.println();
        System.out.println();


        start = new LongLat(-3.1845,55.9428);
        end   = new LongLat(-3.1924,55.9462);
        pathFinder.createPath(start, end);
        path=pathFinder.getPath();
        System.out.println(path.size());
        System.out.print("[");
        for (LongLat x: path){
            System.out.print("["+ x.lng +","+ x.lat +"]"+",");
        }
        System.out.println();
        angleList = pathFinder.getAngleList();
        System.out.println(angleList.size());
        for (int x: angleList){
            System.out.print(x+" ");
        }
        System.out.println();
        System.out.println("Straight line:" + start.distanceTo(end)/0.00015);
        System.out.println();
    }

    @Test
    public void checkDrone(){
        What3WordsConverter converter = new What3WordsConverter("localhost", "9898");
        Menus menus = new Menus("localhost","9898");
        String date = "2022-01-01";
        date = "2022-05-14";
        date = "2023-12-31";
        date = "2023-12-27";
        Database database = new Database("localhost", "1527");
        ArrayList<Order> orders = database.getOrders(date, converter, menus);

        GeoJson location = new GeoJson("localhost", "9898");
        NoFlyZone noFlyZone = location.getNoFlyZone();

        Drone drone = new Drone(orders,noFlyZone);
        //System.out.println("Total Number of orders: "+drone.orders.size());
        drone.createFlightPath();
        ArrayList<LongLat> flightPath = drone.getOverallFlightPath();
        ArrayList<Integer> angleList = drone.getOverallAngleList();

        System.out.println("Path size: "+flightPath.size());
        System.out.print("[");
        for (LongLat x: flightPath){
            //if (x.closeTo(drone.appleton)){
                //System.out.print("APPLETON");
            //}
            System.out.print("["+ x.lng +","+ x.lat +"]"+",");
        }
        System.out.println();
        System.out.println("Angle Size: " +angleList.size());
        for (int x: angleList){
            System.out.print(x+" ");
        }

        for (int i = 0; i<flightPath.size()-1; i++){
            if (flightPath.get(i).distanceTo(flightPath.get(i+1))>0.00015001) {
                System.out.println(flightPath.get(i).distanceTo(flightPath.get(i + 1)));
            }
        }

        System.out.println();
    }
    */
}
