package uk.ac.ed.inf;

import java.util.ArrayList;
import java.util.PriorityQueue;

public class PathFinder {
     private Locations.NoFlyZone noFlyZone;
     private LongLat start;
     private LongLat end;
     private Node node;


     public PathFinder(LongLat start, LongLat end, Locations.NoFlyZone noFlyZone){
          this.noFlyZone = noFlyZone;
          PriorityQueue<Node> queue = new PriorityQueue<>(this::compare);
          node = new Node(start, end, queue, new ArrayList<>());
     }
     public int compare(Node x1, Node x2){

          return (int)((x1.score-x2.score));
     }

     private class Node{
          private Locations.NoFlyZone noFlyZone = PathFinder.this.noFlyZone;
          private LongLat currentPosition;
          private LongLat end;
          public PriorityQueue<Node> frontier;
          public ArrayList<LongLat> visitedLocations;
          public double score;

          public Node(LongLat currentPosition, LongLat end, PriorityQueue<Node> frontier, ArrayList<LongLat> visitedLocations){
               this.currentPosition = currentPosition;
               this.end = end;
               this.frontier = frontier;
               this.visitedLocations = visitedLocations;
          }

          private void getNextPositions(){
               ArrayList<Node> nextPositions = new ArrayList<>();
               Node nextNode;
               LongLat possibleNext;
               PriorityQueue<Node> emptyQueue;
               ArrayList<LongLat> newVisitedLocations = visitedLocations;
               newVisitedLocations.add(currentPosition);

               for (int i = 0; i<360; i+=10){
                    possibleNext = currentPosition.nextPosition(i);
                    if (noFlyZone.outOfNoFlyCheck(currentPosition, possibleNext) && notVisited(possibleNext)){
                         emptyQueue = new PriorityQueue<>();
                         nextNode = new Node(possibleNext, end, emptyQueue, newVisitedLocations);
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
