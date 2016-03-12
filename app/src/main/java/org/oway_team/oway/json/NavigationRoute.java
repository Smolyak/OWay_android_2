package org.oway_team.oway.json;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ru.yandex.yandexmapkit.utils.GeoPoint;

public class NavigationRoute {
    /**
     * Total route duration in mins
     */
    private int mTotalDuration;
    /**
     * Total route length in Km's
     */
    private double mTotalDistance;
    /**
     * List of navigation points;
     * It will be points with balloons on map
     */
    private List <NavigationItem> mNavigationPoints;
    /**
     * List of LINESTRINGS which represents parts of route
     */
    private List<NavigationLineString> mLineStrings;

    private String mRouteId;

    private NavigationRoute() {
        mLineStrings = new ArrayList<NavigationLineString>();
        mNavigationPoints = new ArrayList<NavigationItem>();
    }
    public static NavigationRoute fromJSON(String jString) {
        NavigationRoute route = new NavigationRoute();
        try {
            JSONArray jArr;
            JSONObject rootObj = new JSONObject(jString);
            route.mTotalDistance = rootObj.getInt("total_distance")/1000.d;
            route.mTotalDuration = rootObj.getInt("total_duration")/60;
            if (!rootObj.isNull("id")) {
                route.mRouteId = rootObj.getString("id");
            }
            if (!rootObj.isNull("points")) {
                jArr = rootObj.getJSONArray("points");
                for (int i = 0; i < jArr.length(); i++) {
                    JSONObject jItem = jArr.getJSONObject(i);
                    NavigationItem item = NavigationItem.fromJSON(jItem);
                    route.mNavigationPoints.add(item);
                }

            }
            jArr = rootObj.getJSONArray("paths");
            for (int i = 0; i < jArr.length(); i++) {
                NavigationLineString lineString = new NavigationLineString();
                String line = jArr.getString(i);
                line = line.replace("LINESTRING(","");
                line = line.replace(")","");
                String[] coords = line.split(",");
                for (int j = 0; j < coords.length; j++) {
                    String []geo = coords[j].split(" ");
                    double lon = Double.parseDouble(geo[1]);
                    double lat = Double.parseDouble(geo[0]);
                    lineString.points.add(new GeoPoint(lon, lat));

                }
                route.mLineStrings.add(lineString);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return route;
    }

    public int getTotalDuration() {
        return mTotalDuration;
    }

    public double getTotalDistance() {
        return mTotalDistance;
    }
    public List<NavigationItem> getNavigationPoints() {
        return mNavigationPoints;
    }
    public List<NavigationLineString> getLineStringsList() {
        return mLineStrings;
    }


    public String getRouteId() {
        return mRouteId;
    }

    /** Temporarily hack. Must be removed later! **/
    public void setRouteId(String routeId) {
        this.mRouteId = routeId;
    }
}
