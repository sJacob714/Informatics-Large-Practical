package uk.ac.ed.inf;

import java.sql.*;
import java.util.ArrayList;

public class Database {
    public String query;
    public Connection conn = null;
    PreparedStatement psQuery = null;

    public Database(String machineName, String port){
        String jdbcString;
        jdbcString = "jdbc:derby://" +machineName+ ":" +port+ "/derbyDB";
        try {
            conn = DriverManager.getConnection(jdbcString);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public ArrayList<Order> getOrders(String date, What3WordsConverter converter, Menus menus){
        ArrayList<Order> orders = new ArrayList<>();
        try {
            query = "select * from orders where deliveryDate=(?)";
            psQuery = conn.prepareStatement(query);
            psQuery.setString(1, date);

            ResultSet orderResults = psQuery.executeQuery();
            ResultSet orderDetailsResults;

            Order order;
            while (orderResults.next()){
                order = new Order(orderResults, conn, converter, menus);
                orders.add(order);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }



}
