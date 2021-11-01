package uk.ac.ed.inf;

import java.sql.*;
import java.util.ArrayList;

public class Database {
    public ArrayList<Order> orders = new ArrayList<>();

    public Database(String machineName, String port, String date, What3WordsConverter converter){
        String jdbcString;
        String query;
        Connection conn = null;
        Statement statement = null;
        PreparedStatement psQuery = null;

        jdbcString = "jdbc:derby://" +machineName+ ":" +port+ "/derbyDB";
        try {

            conn = DriverManager.getConnection(jdbcString);

            query = "select * from orders where deliveryDate=(?)";
            psQuery = conn.prepareStatement(query);
            psQuery.setString(1, date);

            ResultSet orderResults = psQuery.executeQuery();
            ResultSet orderDetailsResults;

            Order order;
            while (orderResults.next()){
                order = new Order();
                order.orderNo = orderResults.getString("orderNo");
                order.deliveryDate = orderResults.getString("deliveryDate");
                order.customer = orderResults.getString("customer");
                order.deliverTo = converter.convert(orderResults.getString("deliverTo"));

                query = "select * from orderDetails where orderNo=(?)";
                psQuery = conn.prepareStatement(query);
                psQuery.setString(1, order.orderNo);

                orderDetailsResults = psQuery.executeQuery();
                while (orderDetailsResults.next()){
                    order.orderItems.add(orderDetailsResults.getString("item"));
                }
                orders.add(order);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


}
