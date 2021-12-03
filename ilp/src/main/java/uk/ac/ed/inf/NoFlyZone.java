package uk.ac.ed.inf;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;

import java.awt.geom.Line2D;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for storing NoFlyZone and for methods involving the NoFlyZone
 */
public class NoFlyZone {
    private final List<Line2D> noFlyPerimeter = new ArrayList<>();

    /**
     * Class constructor for NoFlyZone.
     * Gets feature collection from HttpResponse.
     * Turns feature collection into list of Line2D objects and saves as noFlyPerimeter.
     *
     * @param response Http response that should have feature collection in the body
     */
    public NoFlyZone(HttpResponse<String> response) {
        FeatureCollection noFlyZone = FeatureCollection.fromJson(response.body());

        Line2D line;
        // for every feature, gets list of points of the feature
        for (Feature feature : noFlyZone.features()) {
            for (List<Point> pointList : ((Polygon) feature.geometry()).coordinates()) {

                // for every adjacent point, creates Line2D between the two points and adds to noFlyPerimeter
                for (int i = 0; i < pointList.size(); i++) {
                    // uses (i+1)%pointList.size() so that there is line from last point to first point
                    line = new Line2D.Double(pointList.get(i).longitude(), pointList.get(i).latitude(),
                            pointList.get((i + 1)% pointList.size()).longitude(),
                            pointList.get((i + 1)% pointList.size()).latitude());
                    noFlyPerimeter.add(line);
                }
            }
        }
    }

    /**
     * Checks if straight path from start to end coordinate stays outside of no-fly zone.
     * Does this by checking if there is line intersection between the
     * path and any of the lines of the noFlyPerimeter.
     *
     * @param start starting location
     * @param end ending location
     * @return True if straight line path stays outside of no-fly zone
     */
    public boolean staysOutOfNoFly(LongLat start, LongLat end){
        // Creates a line between start and end coordinate
        Line2D line = new Line2D.Double(start.getLng(), start.getLat(), end.getLng(), end.getLat());

        // for every line in the perimeter, checks if there is line intersection
        for (Line2D perimeter : noFlyPerimeter) {
            if (line.intersectsLine(perimeter)) {
                return false;
            }
        }
        return true;
    }
}