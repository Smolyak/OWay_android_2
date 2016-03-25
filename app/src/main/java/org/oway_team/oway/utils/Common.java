package org.oway_team.oway.utils;


import android.content.Context;
import android.database.MatrixCursor;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;

import org.oway_team.oway.json.NavigationItem;

import java.util.ArrayList;
import java.util.List;

import ru.yandex.yandexmapkit.utils.GeoPoint;

public class Common {
    public static Drawable getDrawable(Context context, int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return context.getResources().getDrawable(id, context.getTheme());
        } else {
            return context.getResources().getDrawable(id);
        }
    }
    public static MatrixCursor buildSuggestionsCursor(final List<NavigationItem> items) {
        String rows[] = new String[2];
        rows[0] = "_ID";
        rows[1] = "SUGGEST_COLUMN_TEXT_1";
        final MatrixCursor cursor = new MatrixCursor(rows);
        if(items != null) {
            for (int i = 0; i < items.size(); i++) {
                cursor.addRow(new Object[]{Integer.toString(i), items.get(i).title});
            }
        }
        return cursor;
    }

    /**
     *
     * @param itemsList
     * @return List of retreived geopoints
     */
    @org.jetbrains.annotations.NotNull
    public static List<GeoPoint> navItemsToGeoPoints (List<NavigationItem> itemsList) {
        List<GeoPoint> resList = new ArrayList<GeoPoint>();
        GeoPoint location;
        if (itemsList != null) {
            for (NavigationItem item: itemsList) {
                location = item.location;
                if (location.getLat() > 0 && location.getLon() > 0)
                    resList.add(item.location);
            }
        }
        return resList;
    }
}
