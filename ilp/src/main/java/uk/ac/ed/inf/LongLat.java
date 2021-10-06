package uk.ac.ed.inf;

public class LongLat {
    public double longitude;
    public double latitude;

    private final double longitudeUpper = -3.184319;
    private final double longitudeLower = -3.192473;
    private final double latitudeUpper = 55.946233;
    private final double latitudeLower = 55.942617;

    public LongLat(double longitude, double latitude){
        this.longitude = longitude;
        this.latitude = latitude;
    }

    /**
     * Checks whether the instance of LongLat is within the drone confinement area
     *
     * @return True if Coordinates are in confinement are, False otherwise
     */
    public boolean isConfined(){
        boolean confined = (longitude >= longitudeLower)
                && (longitude <= longitudeUpper)
                && (latitude >= latitudeLower)
                && (latitude <= latitudeUpper);
        return confined;
    }

    /**
     * Calculates the pythagorean distance to the destination
     *
     * @param destination longitude and latitude of destination
     * @return pythagorean distance between current position and destination
     */
    public double distanceTo(LongLat destination){
        double longitudeDistance = Math.pow( (destination.longitude-longitude), 2);
        double latitudeDistance = Math.pow( (destination.latitude-latitude) , 2);
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
     * Calculates next position that will be travelled
     *
     * @param angle angle that object is facing
     * @return LongLat object that is the next position
     */
    public LongLat nextPosition(int angle){
        LongLat nextPosition;

        if (angle==-999){
            nextPosition = new LongLat(longitude, latitude);
        }
        else {
            double nextLongitude = longitude + 0.00015 * Math.cos(Math.toRadians(angle));
            double nextLatitude = latitude + 0.00015 * Math.sin(Math.toRadians(angle));
            nextPosition = new LongLat(nextLongitude, nextLatitude);
        }

        return nextPosition;
    }
}
