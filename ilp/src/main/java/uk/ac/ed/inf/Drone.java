package uk.ac.ed.inf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Drone {
    public int battery;
    public ArrayList<Order> orders;
    public ArrayList<Order> deliveredOrders = new ArrayList<>();
    //public Menus menus;
    public PathFinder pathFinder;
    public LongLat currentPosition;

    //For statistics
    public int totalValue;
    public int valueDelivered;
    public int totalNumberOfOrders;
    public int numberOfOrdersDelivered;

    public Order nextOrder;

    public ArrayList<LongLat> overallFlightPath = new ArrayList<>();
    public ArrayList<Integer> overallAngleList = new ArrayList<>();
    public ArrayList<String> overallOrdersList = new ArrayList<>();
    public final LongLat appleton = new LongLat(-3.186874, 55.944494);

    public Drone(ArrayList<Order> orders, NoFlyZone noFlyZone/*, Menus menus*/){
        battery = 1500;
        this.orders = orders;
        //this.menus = menus;
        pathFinder = new PathFinder(noFlyZone);
        currentPosition = appleton;

        totalValue = 0;
        for (Order order: this.orders){
            totalValue+=order.deliveryCost;
        }
        totalNumberOfOrders = orders.size();
    }

    public void createFlightPath(){
        ArrayList<LongLat> possiblePath;
        ArrayList<Integer> possibleAngles;
        //LongLat possibleCurrentPosition = currentPosition;
        boolean continueJourney = true;
        chooseNextBestOrder();
        while(continueJourney){
            possiblePath = new ArrayList<>();
            possibleAngles = new ArrayList<>();
            System.out.println(nextOrder.destinationOrder.size());
            for (LongLat destination: nextOrder.destinationOrder){
                if (possiblePath.size()==0){
                    pathFinder.createPath(currentPosition, destination);
                }
                else {
                    pathFinder.createPath(possiblePath.get(possiblePath.size()-1), destination);
                }
                possiblePath.addAll(pathFinder.getPath());
                possibleAngles.addAll(pathFinder.getAngleList());
                System.out.println(pathFinder.getPath().size() + " " + pathFinder.getAngleList().size());
                //possiblePath.add(possiblePath.get(possiblePath.size()-1));
                possibleAngles.add(-999);
                //possibleCurrentPosition = possiblePath.get(possiblePath.size()-1);
            }

            pathFinder.createPath(possiblePath.get(possiblePath.size()-1), appleton);
            System.out.println(possiblePath.size());

            if ((possibleAngles.size()+pathFinder.getAngleList().size())<battery){
                deliveredOrders.add(nextOrder);
                numberOfOrdersDelivered++;
                valueDelivered+=nextOrder.deliveryCost;
                //System.out.println("Can Travel");
                //currentPosition = new LongLat(possiblePath.get(possiblePath.size()-1).lng, possiblePath.get(possiblePath.size()-1).lat);
                currentPosition = possiblePath.get(possiblePath.size()-1);
                overallFlightPath.addAll(possiblePath);
                overallAngleList.addAll(possibleAngles);
                overallOrdersList.addAll(Collections.nCopies(possibleAngles.size(),nextOrder.orderNo));
                battery -= possibleAngles.size();
                System.out.println("battery Remaining:"+battery);
                System.out.println("Current path size: " +overallFlightPath.size());
                System.out.println("Current angle size: " +overallAngleList.size());
            }
            else{
                //System.out.println("Miss order");
            }
            chooseNextBestOrder();
            if (nextOrder==null){
                continueJourney=false;
            }
        }
        System.out.println();
        System.out.println();
        System.out.println("Orders Delivered: " +numberOfOrdersDelivered+ ", Total Orders: " +totalNumberOfOrders);
        System.out.println("Order Ratio: " + (numberOfOrdersDelivered/(double)totalNumberOfOrders));
        System.out.println("Value Delivered: " +valueDelivered+ ", Total Value: " +totalValue);
        System.out.println("Value Ratio: " + (valueDelivered/(double)totalValue));

        pathFinder.createPath(currentPosition, appleton);
        overallFlightPath.addAll(pathFinder.getPath());
        overallAngleList.addAll(pathFinder.getAngleList());
        overallOrdersList.addAll(Collections.nCopies(pathFinder.getAngleList().size(), "ReturnAT"));
        battery-=pathFinder.getAngleList().size();
        System.out.println("battery Remaining:"+battery+ "   path: " + overallFlightPath.size());
        System.out.println();
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

    public void chooseNextBestOrder(){
        double bestScore=0;
        nextOrder = null;
        LongLat lastShop;
        LongLat closestShop;
        LongLat nextOrderClosestShop;
        LongLat nextOrderLastShop;
        //ArrayList<LongLat> tempShops;
        double batteryUsageEstimate;
        double score;

        for (int i = 0; i<orders.size(); i++){
            //tempShops = orders.get(i).shopCoordinates.clone();
            closestShop = orders.get(i).shopCoordinates.get(0);
            lastShop = orders.get(i).shopCoordinates.get(0);
            if (orders.get(i).shopCoordinates.size()==2) {
                lastShop = orders.get(i).shopCoordinates.get(1);
                if (currentPosition.distanceTo(orders.get(i).shopCoordinates.get(1)) < currentPosition.distanceTo(closestShop)) {
                    closestShop = orders.get(i).shopCoordinates.get(1);
                    lastShop = orders.get(i).shopCoordinates.get(0);
                }
            }
            //tempShops.remove(closestShop);
            //lastShop = tempShops.get(0);
            batteryUsageEstimate = Math.ceil((currentPosition.distanceTo(closestShop) + orders.get(i).distanceBetweenShops + lastShop.distanceTo(orders.get(i).deliverTo))/0.00015);
            score = orders.get(i).deliveryCost/batteryUsageEstimate;
            /*
            batteryUsageEstimate += Math.ceil(orders.get(i).deliverTo.distanceTo(appleton)/0.00015);
            if (batteryUsageEstimate>battery){
                orders.remove(i);
                i--;
                continue;
            }
             */
            if (score>bestScore){
                bestScore = score;
                nextOrderClosestShop = new LongLat(closestShop.lng, closestShop.lat);
                nextOrderLastShop = new LongLat(lastShop.lng, lastShop.lat);
                nextOrder = orders.get(i);
                if (nextOrder.shopCoordinates.size()==2) {
                    nextOrder.destinationOrder = new ArrayList<>(Arrays.asList(nextOrderClosestShop, nextOrderLastShop, nextOrder.deliverTo));
                }
                else{
                    nextOrder.destinationOrder = new ArrayList<>(Arrays.asList(nextOrderClosestShop, nextOrder.deliverTo));
                }
            }
        }
        orders.remove(nextOrder);
        }

}