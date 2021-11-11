package uk.ac.ed.inf;

import java.util.ArrayList;
import java.util.PriorityQueue;

public class PathFinder {
     private Locations.NoFlyZone noFlyZone;
     private Node node;


     public PathFinder(Locations.NoFlyZone noFlyZone){
          this.noFlyZone = noFlyZone;
     }

     public ArrayList<LongLat> findPath(LongLat start, LongLat end){
          PriorityQueue<Node> queue = new PriorityQueue<>();
          node = new Node(start, end, queue, new ArrayList<>());
          node.visitedLocations.add(start);
          node.getNextPositions();
          return node.traverseFrontier();
     }

     private int compare(Node x, Node y){
          double xScore = (x.visitedLocations.size()*0.00015) + x.currentPosition.distanceTo(x.end);
          double yScore = (y.visitedLocations.size()*0.00015) + y.currentPosition.distanceTo(y.end);
          return (int)((xScore-yScore)*1000000);
     }

     private class Node implements Comparable<Node>{
          private Locations.NoFlyZone noFlyZone = PathFinder.this.noFlyZone;
          private LongLat currentPosition;
          private LongLat end;
          public PriorityQueue<Node> frontier;
          public ArrayList<LongLat> visitedLocations;

          public Node(LongLat currentPosition, LongLat end, PriorityQueue<Node> frontier, ArrayList<LongLat> visitedLocations){
               this.currentPosition = currentPosition;
               this.end = end;
               this.frontier = frontier;
               this.visitedLocations = visitedLocations;
               //this.visitedLocations.add(currentPosition);
          }

          @Override
          public int compareTo(Node node){
               double xScore = (this.visitedLocations.size()*0.00015) + this.currentPosition.distanceTo(this.end);
               double yScore = (node.visitedLocations.size()*0.00015) + node.currentPosition.distanceTo(node.end);
               return (int)((xScore-yScore)*1000000000);
          }

          public ArrayList<LongLat> traverseFrontier(){
               System.out.println(visitedLocations.size());
               Node nextNode;
               ArrayList<LongLat> returnedList;

               while (!frontier.isEmpty()) {
                    nextNode = frontier.remove();
                    nextNode.frontier = frontier;

                    if (nextNode.currentPosition.closeTo(end)){
                         System.out.println("FOUND FOUND FOUND FOUND FOUND FOUND");
                         return nextNode.visitedLocations;
                         //ArrayList<LongLat> found = new ArrayList<>();
                         //found.add(nextNode.currentPosition);
                         //found.add(this.currentPosition);
                         //return found;
                    }

                    nextNode.visitedLocations.add(nextNode.currentPosition);
                    nextNode.getNextPositions();
                    returnedList = nextNode.traverseFrontier();
                    if (returnedList.size()!=0){
                         //returnedList.add(this.currentPosition);
                         return returnedList;
                    }
               }
               System.out.println("returned empty");
               return new ArrayList<>();
          }

          public void getNextPositions(){
               Node nextNode;
               LongLat possibleNext;
               PriorityQueue<Node> emptyQueue = new PriorityQueue<>();

               for (int i = 0; i<360; i+=10){
                    possibleNext = currentPosition.nextPosition(i);
                    if (possibleNext.isConfined() && noFlyZone.outOfNoFlyCheck(currentPosition, possibleNext) && notVisited(possibleNext)){
                         nextNode = new Node(possibleNext, end, emptyQueue, visitedLocations);
                         frontier.add(nextNode);
                         //System.out.println(i);
                         //System.out.println(nextNode.currentPosition.lat +","+ nextNode.currentPosition.lng);
                    }
               }
          }

          private boolean notVisited(LongLat position){
               for (LongLat previous: visitedLocations){
                    if (position.closeTo(previous)){
                         return false;
                    }
               }
               return true;
          }
     }

}
