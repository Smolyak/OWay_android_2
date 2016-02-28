package org.oway_team.oway.maps;
import android.content.Context;

import org.oway_team.oway.json.JSONLineString;

import java.util.ArrayList;
import java.util.List;

import ru.yandex.yandexmapkit.MapController;
import ru.yandex.yandexmapkit.overlay.Overlay;
import ru.yandex.yandexmapkit.overlay.OverlayItem;
import ru.yandex.yandexmapkit.utils.GeoPoint;

public class OverlayRect extends Overlay {

    OverlayRectItem overlayRectItem;
    Context mContext;
    MapController mMapController;
    RectRender rectRender;
    JSONLineString mLinestring;

    public OverlayRect(MapController mapController, JSONLineString lineString) {
        super(mapController);
        mMapController = mapController;
        mContext = mapController.getContext();
        rectRender = new RectRender();
        setIRender(rectRender);
        mLinestring = lineString;
        // TODO Auto-generated constructor stub
        if(lineString.points.size() > 0) {
            overlayRectItem = new OverlayRectItem(new GeoPoint(0,0), mContext.getResources().getDrawable(android.R.drawable.btn_star));
            overlayRectItem.geoPoint.addAll(lineString.points);
            addOverlayItem(overlayRectItem);
        }
    }

    @Override
    public List<OverlayItem> prepareDraw() {
        // TODO Auto-generated method stub
        ArrayList<OverlayItem> draw = new ArrayList<OverlayItem>();
        overlayRectItem.screenPoint.clear();
        for( GeoPoint point : overlayRectItem.geoPoint){
            overlayRectItem.screenPoint.add(mMapController.getScreenPoint(point));
        }
        draw.add(overlayRectItem);

        return draw;
    }


}