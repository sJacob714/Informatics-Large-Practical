package uk.ac.ed.inf;

import java.sql.*;
import java.util.ArrayList;

public class Database {
    public String query;
    public Connection conn = null;
    Statement statement;
    PreparedStatement psQuery = null;

    public Database(String machineName, String port){
        String jdbcString;
        jdbcString = "jdbc:derby://" +machineName+ ":" +port+ "/derbyDB";
        try {
            conn = DriverManager.getConnection(jdbcString);
            statement = conn.createStatement();
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

    public void writeToDatabase(ArrayList<Order> deliveredOrders, ArrayList<LongLat> flightPath, ArrayList<Integer> angleList, ArrayList<String> orderList){
        try {
            //Checks if deliveries table already exists and deletes if deliveries exists
            DatabaseMetaData databaseMetaData = conn.getMetaData();
            ResultSet resultSet =
                    databaseMetaData.getTables(null, null, "DELIVERIES", null);
            if (resultSet.next()){
                statement.execute("drop table deliveries");
            }
            //Checks if flightpath table already exists and deletes if flightpath exists
            databaseMetaData = conn.getMetaData();
            resultSet =
                    databaseMetaData.getTables(null, null, "FLIGHTPATH", null);
            if (resultSet.next()){
                statement.execute("drop table flightpath");
            }


            statement.execute(
                    "create table deliveries("+
                            "orderNo char(8),"+
                            "deliveredTo varchar(19),"+
                            "costInPence int)");
            psQuery = conn.prepareStatement(
                    "insert into deliveries values (?, ?, ?)");
            for (Order order : deliveredOrders){
                psQuery.setString(1, order.orderNo);
                psQuery.setString(2, order.words);
                psQuery.setInt(3, order.deliveryCost);
                psQuery.execute();
            }

            statement.execute(
                    "create table flightpath("+
                            "orderNo char(8),"+
                            "fromLongitude double,"+
                            "fromLatitude double,"+
                            "angle int,"+
                            "toLongitude double,"+
                            "toLatitude double)");
            psQuery = conn.prepareStatement(
                    "insert into flightpath values (?, ?, ?, ?, ?, ?)");
            for (int i = 0; i<angleList.size(); i++) {
                psQuery.setString(1, orderList.get(i));
                psQuery.setDouble(2, flightPath.get(i).lng);
                psQuery.setDouble(3, flightPath.get(i).lat);
                psQuery.setInt(4, angleList.get(i));
                psQuery.setDouble(5, flightPath.get(i+1).lng);
                psQuery.setDouble(6, flightPath.get(i+1).lat);
                psQuery.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
