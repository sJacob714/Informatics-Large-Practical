package uk.ac.ed.inf;

//Used to parse in What3Words from Json
public class What3Word {
    public String country;
    public Square square;
    public String nearestPlace;
    public LongLat coordinates;
    public String words;
    public String language;
    public String map;

    public static class Square{
        LongLat southwest;
        LongLat northeast;


    }
}
