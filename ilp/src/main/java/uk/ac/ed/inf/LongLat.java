package uk.ac.ed.inf;

public class LongLat {
    // longitude and latitude values of the coordinate
    private final double lng;
    private final double lat;

    // Upper and lower limits of the longitude and latitude
    private final double longitudeUpperLimit = -3.184319;
    private final double longitudeLowerLimit = -3.192473;
    private final double latitudeUpperLimit = 55.946233;
    private final double latitudeLowerLimit = 55.942617;

    private final double move = 0.00015;
    private final int hoverAngle = -999;

    /**
     * Constructor for LongLat class
     *
     * @param lng longitude value of the coordinate
     * @param lat latitude value of the coordinate
     */
    public LongLat(double lng, double lat){
        this.lng = lng;
        this.lat = lat;
    }

    /**
     * Checks whether instance of LongLat is within drone confinement area
     *
     * @return True if Coordinates are in confinement area, False otherwise
     */
    public boolean isConfined(){
        return (lng > longitudeLowerLimit)
                && (lng < longitudeUpperLimit)
                && (lat > latitudeLowerLimit)
                && (lat < latitudeUpperLimit);
    }

    /**
     * Calculates pythagorean distance to the destination
     *
     * @param destination longitude and latitude of destination
     * @return distance between current LongLat instance and destination
     */
    public double distanceTo(LongLat destination){
        // calculates horizontal and vertical aspects of pythagoras equation
        double longitudeDistance = Math.pow( (destination.lng - lng), 2);
        double latitudeDistance = Math.pow( (destination.lat - lat) , 2);

        // calculate and returns final pythagorean distance
        return Math.sqrt( (longitudeDistance + latitudeDistance) );
    }

    /**
     * Checks if current instance of location is within one move (0.00015 degrees) of another instance of location.
     *
     * @param location longitude and latitude of location that is checked if close
     * @return True if within one move, false otherwise
     */
    public boolean closeTo(LongLat location){
        return distanceTo(location) < move;
    }

    /**
     * Calculates next coordinate travelled to, given angle of travel
     *
     * @param angle angle of travel
     * @return LongLat object that is the next position
     */
    public LongLat nextPosition(int angle){
        LongLat nextPosition;

        // Checks if drone is just hovering and wont move
        if (angle==hoverAngle){
            nextPosition = new LongLat(lng, lat);
        }
        // else, calculates longitude and latitude attribute of position being travelled to
        else {
            double nextLongitude = lng + move * Math.cos(Math.toRadians(angle));
            double nextLatitude = lat + move * Math.sin(Math.toRadians(angle));
            nextPosition = new LongLat(nextLongitude, nextLatitude);
        }
        return nextPosition;
    }

    public double getLng() {
        return lng;
    }

    public double getLat() {
        return lat;
    }
}
