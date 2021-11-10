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
          PriorityQueue<Node> queue = new PriorityQueue<>(this::compare);
          node = new Node(start, end, queue, new ArrayList<>());
          return node.traverseFrontier();
     }

     private int compare(Node x, Node y){
          double xScore = (x.visitedLocations.size()*0.00015) + x.currentPosition.distanceTo(x.end);
          double yScore = (y.visitedLocations.size()*0.00015) + y.currentPosition.distanceTo(y.end);
          return (int)((xScore-yScore)*1000000);
     }

     private class Node{
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
               this.visitedLocations.add(currentPosition);
               getNextPositions();
          }

          public ArrayList<LongLat> traverseFrontier(){
               Node nextNode;
               ArrayList<LongLat> returnedList;
               while (!frontier.isEmpty()) {
                    nextNode = frontier.remove();
                    nextNode.frontier = frontier;
                    if (nextNode.currentPosition.closeTo(end)){
                         return nextNode.visitedLocations;
                    }
                    returnedList = nextNode.traverseFrontier();

                    if (returnedList.size()!=0){
                         return returnedList;
                    }
               }
               return new ArrayList<>();
          }

          public void getNextPositions(){
               Node nextNode;
               LongLat possibleNext;
               PriorityQueue<Node> emptyQueue = new PriorityQueue<>();

               for (int i = 0; i<360; i+=10){
                    possibleNext = currentPosition.nextPosition(i);
                    if (noFlyZone.outOfNoFlyCheck(currentPosition, possibleNext) && notVisited(possibleNext)){
                         nextNode = new Node(possibleNext, end, emptyQueue, visitedLocations);
                         frontier.add(nextNode);
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
