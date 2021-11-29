package uk.ac.ed.inf;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;
import uk.ac.ed.inf.LongLat;

import java.awt.geom.Line2D;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class NoFlyZone {
    private FeatureCollection noFlyZone;
    private List<Line2D> noFlyPerimeter = new ArrayList<>();

    public NoFlyZone(HttpResponse<String> response) {
        noFlyZone = FeatureCollection.fromJson(response.body());

        for (Feature feature : noFlyZone.features()) {
            for (List<Point> pointList : ((Polygon) feature.geometry()).coordinates()) {
                for (int i = 0; i < pointList.size() - 1; i++) {
                    ;
                    noFlyPerimeter.add(new Line2D.Double(pointList.get(i).longitude(), pointList.get(i).latitude(), pointList.get(i + 1).longitude(), pointList.get(i + 1).latitude()));
                }
            }
        }
    }

    //TODO: REMOVE THIS IS FOR TESTING ONLY
    public void setNoFlyPerimeter(List<Line2D> perimeter){
        noFlyPerimeter = perimeter;
    }

    /**
     * Checks if straight line path from start to end stays outside of no-fly zone
     *
     * @param start starting location
     * @param end ending location
     * @return True if straight line path stays outside of no-fly zone
     */
    public boolean outOfNoFlyCheck (LongLat start, LongLat end){
        Line2D line = new Line2D.Double(start.lng, start.lat, end.lng, end.lat);

        for (Line2D perimeter : noFlyPerimeter) {
            if (line.intersectsLine(perimeter)) {
                return false;
            }
        }

        return true;
    }
}