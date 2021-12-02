package uk.ac.ed.inf;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Order {
    public final String orderNo;
    public final LongLat deliverTo;
    public final String words;
    public final int deliveryCost;
    public final ArrayList<LongLat> shopCoordinates;
    public final ArrayList<String> orderItems = new ArrayList<>();
    public final double distanceBetweenShops;
    public ArrayList<LongLat> destinationOrder;

    /**
     * Constructor for class
     * Uses result set from order table in database to get basic information on orders.
     * Sends query for orderDetails table in database to get rest of the data needed.
     * Uses these to create needed variables.
     *
     * @param orderResults order information from the order table in the database
     * @param conn connection to derby database
     * @param converter used to convert What3Words
     * @param menus has all details on menu items
     * @throws SQLException if it fails to connect to the database
     */
    public Order(ResultSet orderResults, Connection conn,
                 What3WordsConverter converter, Menus menus) throws SQLException {
        What3Word what3Word;
        orderNo = orderResults.getString("orderNo");

        //gets information from What3Words, find centre of square and saves into deliverTo variable
        words = orderResults.getString("deliverTo");
        what3Word = converter.convert(words);
        deliverTo = what3Word.getCentreCoordinate();

        //Queries database for orderDetails and saves into orderItems list
        String query = "select * from orderDetails where orderNo=(?)";
        PreparedStatement psQuery = conn.prepareStatement(query);
        psQuery.setString(1, orderNo);
        ResultSet orderDetailsResults = psQuery.executeQuery();
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
}
