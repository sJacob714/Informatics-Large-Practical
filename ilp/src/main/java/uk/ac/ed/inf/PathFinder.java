package uk.ac.ed.inf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;

public class PathFinder {
     private NoFlyZone noFlyZone;
     private Node node;
     private Node finalNode;
     private ArrayList<LongLat> path;
     private ArrayList<Integer> angleList;

     public PathFinder(NoFlyZone noFlyZone){
          this.noFlyZone = noFlyZone;
     }

     public void createPath(LongLat start, LongLat end){
          path = new ArrayList<>();
          angleList = new ArrayList<>();
          if (start.closeTo(end)){
               path.add(start);
               return;
          }
          PriorityQueue<Node> queue = new PriorityQueue<>();
          node = new Node(start, null, end, queue, new ArrayList<>(), -999);
          node.visitedLocations.add(start);
          node.getNextPositions();
          finalNode = node.traverseFrontier();
          finalNode.constructPath();
          path = finalNode.getPath();
          angleList = finalNode.getAngleList();

          if (false){
               finalNode.optimizePath(start, path);
          }
     }

     public ArrayList<LongLat> getPath(){
          return path;
     }

     public ArrayList<Integer> getAngleList(){
          return angleList;
     }

     private class Node implements Comparable<Node>{
          private NoFlyZone noFlyZone = PathFinder.this.noFlyZone;
          private LongLat currentPosition;
          public Node previousNode;
          private LongLat end;
          public PriorityQueue<Node> frontier;
          public ArrayList<LongLat> visitedLocations;
          public int distanceTravelled;
          public int previousAngle;
          public ArrayList<Integer> angleList;
          public ArrayList<LongLat> traversedPath;

          public Node(LongLat currentPosition,Node previousNode, LongLat end, PriorityQueue<Node> frontier, ArrayList<LongLat> visitedLocations, int previousAngle){
               this.currentPosition = currentPosition;
               this.previousNode = previousNode;
               this.end = end;
               this.frontier = frontier;
               this.visitedLocations = visitedLocations;
               this.previousAngle = previousAngle;
          }

          @Override
          public int compareTo(Node node){
               double score1 = this.currentPosition.distanceTo(this.end);
               double score2 = node.currentPosition.distanceTo(node.end);
               return (int)((score1-score2)*1000000000);
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
                         //System.out.println("FOUND FOUND FOUND FOUND FOUND FOUND");
                         return nextNode;
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
                         //visitedLocations.add(possibleNext);
                         nextNode = new Node(possibleNext, this, end, emptyQueue, visitedLocations, i);
                         frontier.add(nextNode);
                         //System.out.println(i);
                         //System.out.println(nextNode.currentPosition.lat +","+ nextNode.currentPosition.lng);
                    }
               }
          }

          public void constructPath(){
               Node node = this;
               angleList = new ArrayList<>();
               traversedPath = new ArrayList<>();
               traversedPath.add(currentPosition);
               angleList.add(previousAngle);
               while (node.previousNode!=null){
                    node = node.previousNode;
                    traversedPath.add(node.currentPosition);
                    angleList.add(node.previousAngle);
               }
               Collections.reverse(traversedPath);
               Collections.reverse(angleList);
               angleList.remove(0);
          }

          public ArrayList<LongLat> getPath(){
               return traversedPath;
          }

          public ArrayList<Integer> getAngleList(){
               return angleList;
          }

          public ArrayList<LongLat> optimizePath(LongLat start, ArrayList<LongLat> path){
               ArrayList<LongLat> optimizedPath = new ArrayList<LongLat>();
               LongLat coordinate = start;
               Collections.reverse(path);
               for (LongLat point: path){
                    if (!noFlyZone.outOfNoFlyCheck(point, start)){
                         optimizedPath.add(point);
                    }
                    else{
                         coordinate = point;
                         break;
                    }
               }
               //optimizedPath.addAll(findPath(coordinate, start, false));
               //Collections.
               return null;
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
