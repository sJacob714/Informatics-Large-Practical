package uk.ac.ed.inf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Drone {
    private int battery;
    private final ArrayList<Order> orders;
    private final ArrayList<Order> deliveredOrders = new ArrayList<>();
    private final PathFinder pathFinder;
    private LongLat currentPosition;
    private Order nextOrder;

    //For statistics
    private int totalValue;
    private int valueDelivered;
    private final int totalNumberOfOrders;
    private int numberOfOrdersDelivered;

    //Needed for final outputs
    private final ArrayList<LongLat> overallFlightPath = new ArrayList<>();
    private final ArrayList<Integer> overallAngleList = new ArrayList<>();
    private final ArrayList<String> overallOrdersList = new ArrayList<>();

    //Constants
    private final int initialBattery = 1500;
    private final int hoverAngle = -999;
    private final LongLat appleton = new LongLat(-3.186874, 55.944494);

    /**
     * Constructor for Drone Class
     * Sets battery to initial value of 1500 and stores other required attributes
     *
     * @param orders orders to be fulfilled
     * @param noFlyZone noFlyZone drone cannot enter
     */
    public Drone(ArrayList<Order> orders, NoFlyZone noFlyZone){
        battery = initialBattery;
        this.orders = orders;
        pathFinder = new PathFinder(noFlyZone);
        currentPosition = appleton;

        //Finds total value of all the orders
        totalValue = 0;
        for (Order order: this.orders){
            totalValue+=order.getDeliveryCost();
        }
        totalNumberOfOrders = orders.size();
    }

    /**
     * Finds flight path for drone to take.
     *
     * Gets roughly the next best order to deliver.
     * Finds path to go to each shop of the order and delivery location,
     * If the battery needed to make this order and return to appleton are less than batter remaining:
     *      add the path and the angles to the overall path and angle list
     * move on to the next best order and repeat
     */
    public void createFlightPath(){
        ArrayList<LongLat> possiblePath;
        ArrayList<Integer> possibleAngles;

        chooseNextBestOrder();
        //Until there are no orders remaining
        while(nextOrder!=null){
            possiblePath = new ArrayList<>();
            possibleAngles = new ArrayList<>();

            // for each location that need to be travelled to for the order
            for (LongLat destination: nextOrder.getDestinationOrder()){
                // if possiblePath is size 0, pathfinder should start from current position,
                // else pathfinder should start from the last coordinate in possible path
                if (possiblePath.size()==0){
                    pathFinder.createPath(currentPosition, destination);
                }
                else {
                    pathFinder.createPath(possiblePath.get(possiblePath.size()-1), destination);
                }
                possiblePath.addAll(pathFinder.getPath());
                possibleAngles.addAll(pathFinder.getAngleList());
                //append hover angle as drone needs to hover to pickup/deliver
                possibleAngles.add(hoverAngle);
            }

            //Check if battery usage needed to make order and return to appleton is greater than battery remaining
            pathFinder.createPath(possiblePath.get(possiblePath.size()-1), appleton);
            if ((possibleAngles.size() + pathFinder.getAngleList().size()) < battery){
                // for statistics
                deliveredOrders.add(nextOrder);
                numberOfOrdersDelivered++;
                valueDelivered += nextOrder.getDeliveryCost();

                // update current position, lists and battery
                currentPosition = possiblePath.get(possiblePath.size()-1);
                overallFlightPath.addAll(possiblePath);
                overallAngleList.addAll(possibleAngles);
                overallOrdersList.addAll(Collections.nCopies(possibleAngles.size(),nextOrder.getOrderNo()));
                battery -= possibleAngles.size();

                System.out.println("Battery Remaining: "+battery);
                System.out.println("Current path size: " +overallFlightPath.size());
                System.out.println("Current angle size: " +overallAngleList.size());
                System.out.println();
            }
            chooseNextBestOrder();
        }

        System.out.println("Orders Delivered: " +numberOfOrdersDelivered+ ", Total Orders: " +totalNumberOfOrders);
        System.out.println("Order Ratio: " + (numberOfOrdersDelivered/(double)totalNumberOfOrders)*100+" %");
        System.out.println("Value Delivered: " +valueDelivered+ ", Total Value: " +totalValue);
        System.out.println("Value Ratio: " + (valueDelivered/(double)totalValue)*100+" %");

        // Finds path to return to appleton and adds info to lists
        pathFinder.createPath(currentPosition, appleton);
        currentPosition = overallFlightPath.get(overallFlightPath.size()-1);
        overallFlightPath.addAll(pathFinder.getPath());
        overallAngleList.addAll(pathFinder.getAngleList());
        overallOrdersList.addAll(Collections.nCopies(pathFinder.getAngleList().size(), "ReturnAT"));
        battery -= pathFinder.getAngleList().size();

        System.out.println();
        System.out.println("Battery Remaining: "+battery);
    }

    /**
     * Estimates next best order to deliver to
     *
     * For every order:
     * Checks if deliveryCost by distance needed to travel, is better than previously best order
     * If it is, save order as best order
     */
    private void chooseNextBestOrder(){
        double bestScore=0;
        nextOrder = null;
        LongLat lastShop;
        LongLat closestShop;
        LongLat nextOrderClosestShop;
        LongLat nextOrderLastShop;
        ArrayList<LongLat> nextOrderShopCoordinates;
        double batteryUsageEstimate;
        double score;

        for (Order order : orders) {
            nextOrderShopCoordinates = order.getShopCoordinates();

            // sets closest and last shop to first element in shopCoordinates for if there's only one shop
            closestShop = nextOrderShopCoordinates.get(0);
            lastShop = nextOrderShopCoordinates.get(0);
            // if there are two shops, orders shops based on what is closest to current position
            if (nextOrderShopCoordinates.size() == 2) {
                lastShop = nextOrderShopCoordinates.get(1);
                if (currentPosition.distanceTo(nextOrderShopCoordinates.get(1)) < currentPosition.distanceTo(closestShop)) {
                    closestShop = nextOrderShopCoordinates.get(1);
                    lastShop = nextOrderShopCoordinates.get(0);
                }
            }

            //calculates score based on delivery cost by estimated battery usage
            batteryUsageEstimate = Math.ceil((currentPosition.distanceTo(closestShop)
                    + order.getDistanceBetweenShops() + lastShop.distanceTo(order.getDeliverTo())) / 0.00015);
            score = order.getDeliveryCost() / batteryUsageEstimate;

            //if score is better, updates best score and best order details
            if (score > bestScore) {
                bestScore = score;
                nextOrderClosestShop = closestShop;
                nextOrderLastShop = lastShop;
                nextOrder = order;

                // saves order of destinations to be used later when creating the flight path
                if (nextOrder.getShopCoordinates().size() == 2) {
                    nextOrder.setDestinationOrder(new ArrayList<>(Arrays.asList(nextOrderClosestShop, nextOrderLastShop, nextOrder.getDeliverTo())));
                } else {
                    nextOrder.setDestinationOrder(new ArrayList<>(Arrays.asList(nextOrderClosestShop, nextOrder.getDeliverTo())));
                }
            }
        }
        orders.remove(nextOrder);
        }

    public ArrayList<LongLat> getOverallFlightPath(){
        return overallFlightPath;
    }

    public ArrayList<Integer> getOverallAngleList(){
        return overallAngleList;
    }

    public ArrayList<Order> getDeliveredOrders(){
        return deliveredOrders;
    }

    public ArrayList<String> getOverallOrdersList(){
        return overallOrdersList;
    }


}