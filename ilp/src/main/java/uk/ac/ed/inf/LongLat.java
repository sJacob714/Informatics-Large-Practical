package uk.ac.ed.inf;

public class LongLat {
    // longitude and latitude values of the coordinate
    public double lng;
    public double lat;

    // Upper and lower limits of the longitude and latitude
    private final double longitudeUpper = -3.184319;
    private final double longitudeLower = -3.192473;
    private final double latitudeUpper = 55.946233;
    private final double latitudeLower = 55.942617;

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
     * Checks whether instance of LongLat is within the drone confinement area
     *
     * @return True if Coordinates are in confinement area, False otherwise
     */
    public boolean isConfined(){
        boolean confined = (lng > longitudeLower)
                && (lng < longitudeUpper)
                && (lat > latitudeLower)
                && (lat < latitudeUpper);
        return confined;
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

        // calculate final pythagorean distance
        double pythagoreanDistance = Math.sqrt( (longitudeDistance + latitudeDistance) );
        return pythagoreanDistance;
    }

    /**
     * Checks if current location is within 0.00015 degrees of another location
     *
     * @param location longitude and latitude of location that is checked if close
     * @return True if within 0.00015 degrees, false otherwise
     */
    public boolean closeTo(LongLat location){
        return distanceTo(location) < 0.00015;
    }

    /**
     * Calculates next position travelled, given angle of travel
     *
     * @param angle angle of travel
     * @return LongLat object that is the next position
     */
    public LongLat nextPosition(int angle){
        LongLat nextPosition;

        // if angle is -999, drone doesn't travel and just hovers
        if (angle==-999){
            // current longitude and latitude is returned
            nextPosition = new LongLat(lng, lat);
        }
        // else, calculates longitude and latitude attribute of position being travelled to
        else {
            double nextLongitude = lng + 0.00015 * Math.cos(Math.toRadians(angle));
            double nextLatitude = lat + 0.00015 * Math.sin(Math.toRadians(angle));
            nextPosition = new LongLat(nextLongitude, nextLatitude);
        }

        return nextPosition;
    }
}
