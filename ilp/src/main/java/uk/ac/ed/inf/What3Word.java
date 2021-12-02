package uk.ac.ed.inf;

//Used to parse in What3Words from Json
public class What3Word {
    public Square square;

    /**
     * Gets the coordinates of the centre of the square of a What3Word
     *
     * @return LongLat object of the centre of the square
     */
    public LongLat getCentreCoordinate(){
        double centreLng = (square.northeast.lng + square.southwest.lng)/2;
        double centreLat = (square.northeast.lat + square.southwest.lat)/2;
        LongLat centre = new LongLat(centreLng, centreLat);
        return centre;
    }

    public static class Square{
        LongLat southwest;
        LongLat northeast;
    }
}
