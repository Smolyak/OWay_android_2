package org.oway_team.oway.json;


import org.json.JSONException;
import org.json.JSONObject;

import ru.yandex.yandexmapkit.utils.GeoPoint;

public class NavigationItem {
    public String title;
    public String type;
    public String addr;
    public String priority;
    public String id;
    public String todo_list_id;
    public String position;
    public String after;
    public GeoPoint location;

    public static NavigationItem fromJSON(JSONObject jItem) {
        NavigationItem resItem = new NavigationItem();
        try {
            resItem.title = jItem.getString("title");
            resItem.type = jItem.getString("type");
            resItem.addr = jItem.getString("address");
            resItem.id = jItem.getString("id");
            resItem.todo_list_id = jItem.getString("todo_list_id");
            resItem.position = jItem.getString("position");
            resItem.after = jItem.getString("after");
            double lon = 0.0, lat = 0.0;
            if (!jItem.isNull("lon"))
               lon = jItem.getDouble("lon");

            if (!jItem.isNull("lat"))
                lat = jItem.getDouble("lat");
            resItem.location = new GeoPoint(lat, lon);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  resItem;
    }
}
