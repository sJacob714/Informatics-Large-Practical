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
          ArrayList<LongLat> path = new ArrayList<>();
          if (start.closeTo(end)){
               path.add(start);
               return path;
          }
          PriorityQueue<Node> queue = new PriorityQueue<>();
          node = new Node(start, null, end, queue, new ArrayList<>());
          node.visitedLocations.add(start);
          node.getNextPositions();
          path = node.traverseFrontier().getPath();
          if (false){
               node.optimizePath(start, path);
          }
          return path;
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
               double score1 = /*(this.visitedLocations.size()*0.00015) + */this.currentPosition.distanceTo(this.end);
               double score2 = /*(node.visitedLocations.size()*0.00015) + */node.currentPosition.distanceTo(node.end);

               /*
               double lineLng = this.currentPosition.lng + (this.currentPosition.lng-this.previousNode.currentPosition.lng)*1000;
               double lineLat = this.currentPosition.lat + (this.currentPosition.lat-this.previousNode.currentPosition.lat)*1000;
               if (noFlyZone.outOfNoFlyCheck(this.currentPosition, new LongLat(lineLng, lineLat))){
                    score1+=100000;
               }
               lineLng = node.currentPosition.lng + (node.currentPosition.lng-node.previousNode.currentPosition.lng)*1000;
               lineLat = node.currentPosition.lat + (node.currentPosition.lat-node.previousNode.currentPosition.lat)*1000;
               if (noFlyZone.outOfNoFlyCheck(node.currentPosition, new LongLat(lineLng, lineLat))){
                    score2+=100000;
               }
                */

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
