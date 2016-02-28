package org.oway_team.oway.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class JSONRequestBuilder {
    public static String buildPointsList(List<JSONNavigationItem> items) {
        JSONObject tmpObject = new JSONObject();
        JSONObject resObject = new JSONObject();
        try {
            JSONArray jsonArray = new JSONArray();
            tmpObject.put("title", "test");
            for (JSONNavigationItem item : items) {
                JSONObject jItem = new JSONObject();
                jItem.put("title", item.title);
                jItem.put("type",item.type);
                jItem.put("position",item.priority);
                jItem.put("lon",item.location.getLon());
                jItem.put("lat",item.location.getLat());
                jsonArray.put(jItem);
            }
            tmpObject.put("items",jsonArray);
            resObject.put("list",tmpObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  resObject.toString();
    }
}
