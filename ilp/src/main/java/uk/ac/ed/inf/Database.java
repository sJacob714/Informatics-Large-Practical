package uk.ac.ed.inf;

import java.sql.*;
import java.util.ArrayList;

/**
 * Used to access Derby database
 */
public class Database {
    private Connection conn = null;
    private Statement statement;
    private PreparedStatement psQuery = null;

    /**
     * Class constructor for Database Object.
     * Creates connection to derby database by creating a jdbc String.
     * Creates a Statement object to run SQL statement commands against database.
     *
     * @param machineName name of machine to connect to
     * @param port port where database is running
     */
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

    /**
     * Queries the database for the orders and orderDetails of required date.
     * Parses in all orders for this date and their details into a ArrayList of Order types.
     *
     * @param date delivery date of orders that need to be queried from database
     * @param converter Used to parse What3Words into a usable format, get LongLat coordinates
     * @param menus Used to get delivery cost and get shop coordinates
     * @return list of orders that need to be delivered on requested date.
     */
    public ArrayList<Order> getOrders(String date, What3WordsConverter converter, Menus menus){
        ArrayList<Order> ordersList = new ArrayList<>();
        try {
            // Queries database for every order that needs to be delivered on the requested date
            String query = "select * from orders where deliveryDate=(?)";
            psQuery = conn.prepareStatement(query);
            psQuery.setString(1, date);
            ResultSet orderResults = psQuery.executeQuery();

            // Creates a new Order instance for every order from database and adds it to ordersList
            Order order;
            while (orderResults.next()){
                order = new Order(orderResults, conn, converter, menus);
                ordersList.add(order);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ordersList;
    }

    /**
     * Creates a deliveries and flightpath table in the database.
     * Fills deliveries table with details on every order that was delivered.
     * Fills flightpath table with details on every move taken by the drone.
     *
     * @param deliveredOrders all orders that had been delivered by the drone
     * @param flightPath the path the drone took to deliver the orders
     * @param angleList list of all the angles that the drone took to move from each coordinate to the next
     * @param orderList list of what order was being fulfilled for each move the drone took
     */
    public void writeToDatabase(ArrayList<Order> deliveredOrders, ArrayList<LongLat> flightPath,
                                ArrayList<Integer> angleList, ArrayList<String> orderList){
        try {
            //Checks if deliveries table already exists and deletes if it exists
            DatabaseMetaData databaseMetaData = conn.getMetaData();
            ResultSet resultSet =
                    databaseMetaData.getTables(null, null, "DELIVERIES", null);
            if (resultSet.next()){
                statement.execute("drop table deliveries");
            }
            //Checks if flightpath table already exists and deletes if it exists
            databaseMetaData = conn.getMetaData();
            resultSet =
                    databaseMetaData.getTables(null, null, "FLIGHTPATH", null);
            if (resultSet.next()){
                statement.execute("drop table flightpath");
            }

            //Creates deliveries table
            statement.execute(
                    "create table deliveries("+
                            "orderNo char(8),"+
                            "deliveredTo varchar(19),"+
                            "costInPence int)");
            System.out.println("Created deliveries table");
            // insert details of every delivered order into the deliveries table
            psQuery = conn.prepareStatement(
                    "insert into deliveries values (?, ?, ?)");
            for (Order order : deliveredOrders){
                psQuery.setString(1, order.getOrderNo());
                psQuery.setString(2, order.getWords());
                psQuery.setInt(3, order.getDeliveryCost());
                psQuery.execute();
            }
            System.out.println("Inserted data into deliveries table");
            System.out.println();

            //Creates flightpath table
            statement.execute(
                    "create table flightpath("+
                            "orderNo char(8),"+
                            "fromLongitude double,"+
                            "fromLatitude double,"+
                            "angle integer,"+
                            "toLongitude double,"+
                            "toLatitude double)");
            System.out.println("Created flightpath table");
            //insert details of every move made by the drone when delivering
            psQuery = conn.prepareStatement(
                    "insert into flightpath values (?, ?, ?, ?, ?, ?)");
            for (int i = 0; i<angleList.size(); i++) {
                psQuery.setString(1, orderList.get(i));
                psQuery.setDouble(2, flightPath.get(i).getLng());
                psQuery.setDouble(3, flightPath.get(i).getLat());
                psQuery.setInt(4, angleList.get(i));
                //flightPath array should be one bigger than angleList and orderList array
                psQuery.setDouble(5, flightPath.get(i+1).getLng());
                psQuery.setDouble(6, flightPath.get(i+1).getLat());
                psQuery.execute();
            }
            System.out.println("Inserted data into flightpath table");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
