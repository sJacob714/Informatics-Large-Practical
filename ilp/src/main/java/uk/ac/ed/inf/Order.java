package uk.ac.ed.inf;

import java.util.ArrayList;

public class Order {
    public String orderNo;
    public String deliveryDate;
    public String customer;
    public What3Word deliverTo;

    public ArrayList<String> orderItems = new ArrayList<>();

    public Order(){
    }

}
