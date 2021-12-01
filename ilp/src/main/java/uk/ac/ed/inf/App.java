package uk.ac.ed.inf;

import java.util.ArrayList;

/**
 * Hello world!
 *
 */
public class App {

    public static void main( String[] args ) {
        String machineName = "localhost";

        //Format dates if needed
        if (args[0].length()==1){
            args[0] = "0"+args[0];
        }
        if (args[1].length()==1){
            args[1] = "0"+args[1];
        }
        String date = args[2]+ "-" +args[1]+ "-" +args[0];
        String webPort = args[3];
        String dbPort = args[4];

        //Initializes required objects
        ArrayList<Order> orders;
        NoFlyZone noFlyZone;
        ArrayList<LongLat> flightPath;
        ArrayList<Integer> angleList;
        ArrayList<Order> deliveredOrders;
        ArrayList<String> ordersList;

        What3WordsConverter converter = new What3WordsConverter(machineName, webPort);
        Menus menus = new Menus(machineName,webPort);
        Database database = new Database(machineName, dbPort);
        GeoJson geoJson = new GeoJson(machineName, webPort);
        noFlyZone = geoJson.getNoFlyZone();

        //Creates Flight path
        orders = database.getOrders(date, converter, menus);
        Drone drone = new Drone(orders,noFlyZone);
        drone.createFlightPath();
        flightPath = drone.getOverallFlightPath();
        angleList = drone.getOverallAngleList();
        deliveredOrders = drone.getDeliveredOrders();
        ordersList = drone.getOverallOrdersList();

        System.out.println("Path size: "+flightPath.size());
        System.out.println("Angle Size: " +angleList.size());
        System.out.println();

        //Outputs to required GeoJson file and database tables
        String fileName = "drone-" +args[0]+ "-" +args[1]+ "-" +args[2]+ ".geojson";
        geoJson.writeToGeoJson(flightPath, fileName);
        database.writeToDatabase(deliveredOrders, flightPath, angleList, ordersList);
    }
}
