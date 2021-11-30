package uk.ac.ed.inf;

import java.util.ArrayList;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) {
        String date = args[2]+ "-" +args[1]+ "-" +args[0];
        String machineName = "localhost";
        String webPort = args[3];
        String dbPort = args[4];
        ArrayList<Order> orders;
        NoFlyZone noFlyZone;
        ArrayList<LongLat> flightPath;
        ArrayList<Integer> angleList;

        What3WordsConverter converter = new What3WordsConverter(machineName, webPort);
        Menus menus = new Menus(machineName,webPort);
        Database database = new Database(machineName, dbPort);
        orders = database.getOrders(date, converter, menus);

        GeoJson geoJson = new GeoJson(machineName, webPort);
        noFlyZone = geoJson.getNoFlyZone();

        Drone drone = new Drone(orders,noFlyZone);
        drone.createFlightPath();
        flightPath = drone.getOverallFlightPath();
        angleList = drone.getOverallAngleList();

        System.out.println("Path size: "+flightPath.size());
        System.out.print("[");
        for (LongLat x: flightPath){
            if (x.closeTo(drone.appleton)){
                //System.out.print("APPLETON");
            }
            System.out.print("["+ x.lng +","+ x.lat +"]"+",");
        }
        System.out.println();
        System.out.println("Angle Size: " +angleList.size());
        for (int x: angleList){
            System.out.print(x+" ");
        }
        /*
        for (int i = 0; i<flightPath.size()-1; i++){
            if (flightPath.get(i).distanceTo(flightPath.get(i+1))>0.00015001) {
                System.out.println(flightPath.get(i).distanceTo(flightPath.get(i + 1)));
            }
        }
        */
        System.out.println();

        ArrayList<Order> deliveredOrders = drone.getDeliveredOrders();
        ArrayList<String> ordersList= drone.getOverallOrdersList();

        String fileName = "drone-" +args[0]+ "-" +args[1]+ "-" +args[2]+ ".geojson";
        geoJson.writeToGeoJson(flightPath, fileName);
        database.writeToDatabase(deliveredOrders, flightPath, angleList, ordersList);
    }
}
