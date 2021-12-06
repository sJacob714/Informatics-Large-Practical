package uk.ac.ed.inf;

import java.util.*;

public class PathFinder {
     private final NoFlyZone noFlyZone;
     private ArrayList<LongLat> path;
     private ArrayList<Integer> angleList;

     /**
      * Constructor for PathFinder class
      * Saves noFlyZone
      *
      * @param noFlyZone NoFlyZone that drone cannot enter
      */
     public PathFinder(NoFlyZone noFlyZone){
          this.noFlyZone = noFlyZone;
     }

     /**
      * Uses node class to find a path between start and end coordinates
      *
      * @param start coordinates of start of path
      * @param end coordinates of where end of path should be
      */
     public void createPath(LongLat start, LongLat end){
          path = new ArrayList<>();
          angleList = new ArrayList<>();
          //if the start is already close to end, just return list with start coordinate
          if (start.closeTo(end)){
               path.add(start);
               return;
          }
          //Creates a priority queue, will return node closest to destination when .remove() is called
          PriorityQueue<Node> queue = new PriorityQueue<>();

          //Creates an initial node and then applies getNextPositions() on it to expand its frontier, sets previous angle to -9999 as dummy value
          Node node = new Node(start, null, end, queue, new ArrayList<>(List.of(start)), -9999);
          node.getNextPositions();
          //traverseFrontier is called on the node, returning the finalNode which is at destination
          Node finalNode = node.traverseFrontier();
          //path is constructed from this final node, path and angle list are stored
          finalNode.constructPath();
          path = finalNode.getPath();
          angleList = finalNode.getAngleList();
     }

     /**
      * @return list of coordinates on the path
      */
     public ArrayList<LongLat> getPath(){
          return path;
     }

     /**
      * @return list of angles of travel for the drone
      */
     public ArrayList<Integer> getAngleList(){
          return angleList;
     }

     private class Node implements Comparable<Node>{
          private final LongLat currentPosition;
          private final Node previousNode;
          private final LongLat end;
          private PriorityQueue<Node> frontier;
          private final ArrayList<LongLat> visitedLocations;
          private final int previousAngle;
          private ArrayList<Integer> angleList;
          private ArrayList<LongLat> traversedPath;

          /**
           * Constructor for Node class
           *
           * @param currentPosition LongLat coordinate of current node
           * @param previousNode previous node that was expanded from to get to current node
           * @param end destination that drone needs to reach
           * @param frontier priority queue of nodes that could be explored, ordered on distance from destination
           * @param visitedLocations list of all LongLats that had previously been explored
           * @param previousAngle angle of travel for drone to get from previous location to current location
           */
          public Node(LongLat currentPosition,Node previousNode, LongLat end,
                      PriorityQueue<Node> frontier, ArrayList<LongLat> visitedLocations, int previousAngle){
               this.currentPosition = currentPosition;
               this.previousNode = previousNode;
               this.end = end;
               this.frontier = frontier;
               this.visitedLocations = visitedLocations;
               this.previousAngle = previousAngle;
          }

          /**
           * Overrides compareTo method. Uses distance to end coordinate to determine order of nodes
           * Used to sort the priority queue
           *
           * @param node node to compare to
           * @return less than 0 if current instance is closer to destination, greater if current instance is further away
           * and 0 if both are equal distance from destination
           */
          @Override
          public int compareTo(Node node){
               double score1 = this.currentPosition.distanceTo(this.end);
               double score2 = node.currentPosition.distanceTo(node.end);
               return (int)((score1-score2)*1000000000);    //multiplied by 1000000000 so detail isn't lost when casting to int
          }

          /**
           * Expands all nodes on the frontier until next node at end point or frontier is empty.
           * For each node in frontier, call nextPositions() on next node and
           * then traverse the next node's frontier
           *
           * @return Node that is at or close to end coordinate
           */
          public Node traverseFrontier(){
               Node nextNode;
               Node returnedNode;

               // Keeps looping until frontier is empty
               while (!frontier.isEmpty()) {
                    // gets next node from frontier (closest node to end)
                    nextNode = frontier.remove();
                    // sets next node's frontier so the newest frontier is passed in
                    nextNode.setFrontier(frontier);
                    nextNode.visitedLocations.add(nextNode.currentPosition);
                    // if next node is close to end, returns next node
                    if (nextNode.currentPosition.closeTo(end)){
                         return nextNode;
                    }
                    // Gets next positions from next node and then traverses the frontier of the next node
                    nextNode.getNextPositions();
                    returnedNode = nextNode.traverseFrontier();
                    // if a node had been returned from traverseFrontier(), returned node is at end so return this returned node
                    if (returnedNode!=null){
                         return returnedNode;
                    }
               }
               // if frontier is empty, there is no path to the end from this node, so return null
               return null;
          }

          /**
           * find all possible next positions from current position and if valid, adds them onto frontier
           */
          public void getNextPositions(){
               Node nextNode;
               LongLat possibleNext;
               // set new nodes frontier to empty queue as any future updates to frontier won't be applied
               // to new node's frontier if it is set to the frontier when node is initialised
               PriorityQueue<Node> emptyQueue = new PriorityQueue<>();

               // uses nextPosition to get coordinate of possible next positions for each angle
               for (int i = 0; i<360; i+=10){
                    possibleNext = currentPosition.nextPosition(i);

                    //Checks possible next coordinate is valid by making sure it is
                    // in confinement area, out of no-fly zone and hasn't been previously visited
                    if (possibleNext.isConfined() && noFlyZone.staysOutOfNoFly(currentPosition, possibleNext)
                            && notVisited(possibleNext)){
                         // if valid, creates new node and adds to frontier
                         nextNode = new Node(possibleNext, this, end, emptyQueue, visitedLocations, i);
                         frontier.add(nextNode);
                    }
               }
          }

          /**
           * Builds back the traversed path and angleList of the drone that got it to the current node
           * Should be used on node that is at end point
           * Reconstructs path from end to start, and then reverses the lists
           */
          public void constructPath(){
               Node node = this;
               angleList = new ArrayList<>();
               traversedPath = new ArrayList<>();

               // add current node and previous angle to traversedPath and angleList
               traversedPath.add(currentPosition);
               angleList.add(previousAngle);

               // until a previous node doesn't exist, recursively goes through the previous
               // nodes, adding previous nodes' position and previous angle to lists
               while (node.previousNode!=null){
                    node = node.previousNode;
                    traversedPath.add(node.currentPosition);
                    angleList.add(node.previousAngle);
               }
               // reverse lists to get path and angle list from start to end
               Collections.reverse(traversedPath);
               Collections.reverse(angleList);
               // as when initialising node, -9999 was used as dummy angle, remove it form start of angle list
               angleList.remove(0);
          }

          /**
           * Checks that coordinate isn't close to any previously visited coordinates.
           * Used to stop backtracking
           *
           * @param position coordinate to check hasn't been visited before
           * @return returns true if not previously visited, false if it has been visited
           */
          private boolean notVisited(LongLat position){
               // iterates through all visited locations, checking if position is close to visited coordinate
               for (LongLat previous: visitedLocations){
                    if (position.closeTo(previous)){
                         return false;
                    }
               }
               return true;
          }

          /**
           * Setter for frontier
           */
          private void setFrontier(PriorityQueue<Node> frontier){
               this.frontier = frontier;
          }

          /**
           * @return traversed path found
           */
          public ArrayList<LongLat> getPath(){
               return traversedPath;
          }

          /**
           * @return angle list of traversed path found
           */
          public ArrayList<Integer> getAngleList(){
               return angleList;
          }
     }

}
