package uk.ac.ed.inf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.PriorityQueue;

public class PathFinder {
     private Locations.NoFlyZone noFlyZone;
     private Node node;


     public PathFinder(Locations.NoFlyZone noFlyZone){
          this.noFlyZone = noFlyZone;
     }

     public ArrayList<LongLat> findPath(LongLat start, LongLat end){
          PriorityQueue<Node> queue = new PriorityQueue<>();
          node = new Node(start, null, end, queue, new ArrayList<>());
          node.visitedLocations.add(start);
          node.getNextPositions();
          return node.traverseFrontier().getPath();
     }

     private int compare(Node x, Node y){
          double xScore = (x.visitedLocations.size()*0.00015) + x.currentPosition.distanceTo(x.end);
          double yScore = (y.visitedLocations.size()*0.00015) + y.currentPosition.distanceTo(y.end);
          return (int)((xScore-yScore)*1000000);
     }

     private class Node implements Comparable<Node>{
          private Locations.NoFlyZone noFlyZone = PathFinder.this.noFlyZone;
          private LongLat currentPosition;
          public Node previousNode;
          private LongLat end;
          public PriorityQueue<Node> frontier;
          public ArrayList<LongLat> visitedLocations;

          public Node(LongLat currentPosition,Node previousNode, LongLat end, PriorityQueue<Node> frontier, ArrayList<LongLat> visitedLocations){
               this.currentPosition = currentPosition;
               this.previousNode = previousNode;
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

          public Node traverseFrontier(){
               //System.out.println(visitedLocations.size());
               Node nextNode;
               Node returnedNode;

               while (!frontier.isEmpty()) {
                    nextNode = frontier.remove();
                    //System.out.println(nextNode.currentPosition.lat+","+nextNode.currentPosition.lng);
                    nextNode.frontier = frontier;
                    nextNode.visitedLocations.add(nextNode.currentPosition);
                    if (nextNode.currentPosition.closeTo(end)){
                         System.out.println("FOUND FOUND FOUND FOUND FOUND FOUND");
                         return nextNode;
                         //ArrayList<LongLat> found = new ArrayList<>();
                         //found.add(nextNode.currentPosition);
                         //found.add(this.currentPosition);
                         //return found;
                    }
                    nextNode.getNextPositions();
                    returnedNode = nextNode.traverseFrontier();
                    if (returnedNode!=null){
                         //returnedList.add(this.currentPosition);
                         return returnedNode;
                    }
               }
               System.out.println("returned empty");
               return null;
          }

          public void getNextPositions(){
               Node nextNode;
               LongLat possibleNext;
               PriorityQueue<Node> emptyQueue = new PriorityQueue<>();

               for (int i = 0; i<360; i+=10){
                    possibleNext = currentPosition.nextPosition(i);
                    if (possibleNext.isConfined() && noFlyZone.outOfNoFlyCheck(currentPosition, possibleNext) && notVisited(possibleNext)){
                         nextNode = new Node(possibleNext, this, end, emptyQueue, visitedLocations);
                         frontier.add(nextNode);
                         //System.out.println(i);
                         //System.out.println(nextNode.currentPosition.lat +","+ nextNode.currentPosition.lng);
                    }
               }
          }

          public ArrayList<LongLat> getPath(){
               Node node = this.previousNode;
               ArrayList<LongLat> traversedPath = new ArrayList<LongLat>();
               traversedPath.add(currentPosition);
               while (node.previousNode!=null){
                    node = node.previousNode;
                    traversedPath.add(node.currentPosition);
               }
               Collections.reverse(traversedPath);
               return traversedPath;
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
