package uk.ac.ed.inf;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Order {
    public String orderNo;
    public String deliveryDate;
    public String customer;
    public LongLat deliverTo;
    public int deliveryCost;
    public ArrayList<LongLat> shopCoordinates;
    public ArrayList<String> orderItems = new ArrayList<>();
    public double distanceBetweenShops=0;
    public ArrayList<LongLat> destinationOrder;

    public Order(ResultSet orderResults, Connection conn, What3WordsConverter converter, Menus menus) throws SQLException {
        orderNo = orderResults.getString("orderNo");
        deliveryDate = orderResults.getString("deliveryDate");
        customer = orderResults.getString("customer");

        What3Word word;
        double centreLng;
        double centreLat;
        word = converter.convert(orderResults.getString("deliverTo"));
        centreLng = (word.square.northeast.lng + word.square.southwest.lng)/2;
        centreLat = (word.square.northeast.lat + word.square.southwest.lat)/2;
        deliverTo = new LongLat(centreLng, centreLat);

        String query = "select * from orderDetails where orderNo=(?)";
        PreparedStatement psQuery = conn.prepareStatement(query);
        psQuery.setString(1, orderNo);

        ResultSet orderDetailsResults = psQuery.executeQuery();
        while (orderDetailsResults.next()){
            orderItems.add(orderDetailsResults.getString("item"));
        }
        deliveryCost = menus.getDeliveryCost(orderItems.toArray(new String[0]));
        shopCoordinates = menus.getCoordinates(converter, orderItems.toArray(new String[0]));
        for (int i =0; i<shopCoordinates.size()-1; i++) {
            distanceBetweenShops += shopCoordinates.get(i).distanceTo(shopCoordinates.get(i+1));
        }
    }
}
