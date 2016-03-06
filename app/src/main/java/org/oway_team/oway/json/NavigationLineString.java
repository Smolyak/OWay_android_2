package org.oway_team.oway.json;

import java.util.ArrayList;
import java.util.List;

import ru.yandex.yandexmapkit.utils.GeoPoint;

public class NavigationLineString {
    public NavigationLineString() {
        points = new ArrayList<GeoPoint>();
    }
    public List<GeoPoint> points;
}
