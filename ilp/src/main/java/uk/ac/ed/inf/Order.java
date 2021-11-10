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
    public What3Word deliverTo;

    public ArrayList<String> orderItems = new ArrayList<>();

    public Order(ResultSet orderResults, Connection conn, What3WordsConverter converter) throws SQLException {
        orderNo = orderResults.getString("orderNo");
        deliveryDate = orderResults.getString("deliveryDate");
        customer = orderResults.getString("customer");
        deliverTo = converter.convert(orderResults.getString("deliverTo"));

        String query = "select * from orderDetails where orderNo=(?)";
        PreparedStatement psQuery = conn.prepareStatement(query);
        psQuery.setString(1, orderNo);

        ResultSet orderDetailsResults = psQuery.executeQuery();
        while (orderDetailsResults.next()){
            orderItems.add(orderDetailsResults.getString("item"));
        }
    }

}
