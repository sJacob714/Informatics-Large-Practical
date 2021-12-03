package uk.ac.ed.inf;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Order {
    private final String orderNo;
    private final LongLat deliverTo;
    private final String words;
    private final int deliveryCost;
    private final ArrayList<LongLat> shopCoordinates;
    private final double distanceBetweenShops;
    private ArrayList<LongLat> destinationOrder;

    /**
     * Constructor for class
     * Uses result set from order table in database to get basic information on orders.
     * Sends query for orderDetails table in database to get rest of the data needed.
     * Uses these to create needed variables.
     *
     * @param orderResult order information from the order table in the database
     * @param conn connection to derby database
     * @param converter used to convert What3Words
     * @param menus has all details on menu items
     * @throws SQLException if it fails to connect to the database
     */
    public Order(ResultSet orderResult, Connection conn,
                 What3WordsConverter converter, Menus menus) throws SQLException {
        What3Word what3Word;
        orderNo = orderResult.getString("orderNo");

        //gets information from What3Words and saves coordinates into deliverTo variable
        words = orderResult.getString("deliverTo");
        what3Word = converter.convert(words);
        deliverTo = what3Word.getCoordinates();

        //Queries database for orderDetails and saves into orderItems list
        String query = "select * from orderDetails where orderNo=(?)";
        PreparedStatement psQuery = conn.prepareStatement(query);
        psQuery.setString(1, orderNo);
        ResultSet orderDetailsResults = psQuery.executeQuery();
        ArrayList<String> orderItems = new ArrayList<>();
        while (orderDetailsResults.next()){
            orderItems.add(orderDetailsResults.getString("item"));
        }

        //Gets delivery cost, shop coordinates and distance between shops if there are two
        deliveryCost = menus.getDeliveryCost(orderItems.toArray(new String[0]));
        shopCoordinates = menus.getShopCoordinates(converter, orderItems.toArray(new String[0]));
        if (shopCoordinates.size()==2){
            distanceBetweenShops = shopCoordinates.get(0).distanceTo(shopCoordinates.get(1));
        }
        else{
            distanceBetweenShops = 0;
        }
    }

    //getters and setters
    public String getOrderNo() {
        return orderNo;
    }

    public LongLat getDeliverTo() {
        return deliverTo;
    }

    public String getWords() {
        return words;
    }

    public int getDeliveryCost() {
        return deliveryCost;
    }

    public ArrayList<LongLat> getShopCoordinates() {
        return shopCoordinates;
    }

    public double getDistanceBetweenShops() {
        return distanceBetweenShops;
    }

    public ArrayList<LongLat> getDestinationOrder() {
        return destinationOrder;
    }

    public void setDestinationOrder(ArrayList<LongLat> destinationOrder) {
        this.destinationOrder = destinationOrder;
    }
}
