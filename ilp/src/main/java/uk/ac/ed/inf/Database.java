package uk.ac.ed.inf;

import java.sql.*;
import java.util.ArrayList;

public class Database {
    public ArrayList<Order> orders = new ArrayList<>();

    public Database(String machineName, String port, String date, What3WordsConverter converter){
        String jdbcString;
        String query;
        Connection conn = null;
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
                order = new Order(orderResults, conn, converter);
                orders.add(order);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


}
