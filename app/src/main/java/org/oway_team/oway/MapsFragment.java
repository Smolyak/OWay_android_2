package org.oway_team.oway;

import android.support.v4.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.oway_team.oway.json.JSONLineString;
import org.oway_team.oway.json.JSONNavigationItem;
import org.oway_team.oway.json.JSONRequestBuilder;
import org.oway_team.oway.json.JSONRoute;
import org.oway_team.oway.json.JSONRouterProxy;
import org.oway_team.oway.json.JSONRouterProxyListener;
import org.oway_team.oway.maps.OverlayRect;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ru.yandex.yandexmapkit.MapController;
import ru.yandex.yandexmapkit.MapView;
import ru.yandex.yandexmapkit.OverlayManager;
import ru.yandex.yandexmapkit.overlay.Overlay;
import ru.yandex.yandexmapkit.overlay.OverlayItem;
import ru.yandex.yandexmapkit.overlay.balloon.BalloonItem;
import ru.yandex.yandexmapkit.utils.GeoPoint;

public class MapsFragment extends Fragment implements JSONRouterProxyListener {
    public static final String TAG = "OWay-Map";
    MapController mMapController;
    LinearLayout mView;
    JSONRouterProxy mJSONRouterProxy;
    OverlayManager mOverlayManager;
    String mRouteId;
    JSONRoute mCurrentRoute;
    MapView mMapView;
    List<Overlay> mCustomOverlays;
    Overlay mPointsOverlay;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.maps_layout, container, false);
        mMapView = (MapView) view.findViewById(R.id.map);
        mMapView.showBuiltInScreenButtons(true);
        mJSONRouterProxy = new JSONRouterProxy(this);
        mMapController = mMapView.getMapController();
        //Start map on Novosibirsk
        mMapController.setPositionAnimationTo(new GeoPoint(55.018803,82.933952));
        mOverlayManager = mMapController.getOverlayManager();

        mCustomOverlays = new ArrayList<Overlay>();
        return view;
    }
    public void clearOverlays() {
        if (mOverlayManager == null || mMapController == null || mCustomOverlays.size() == 0)
            return;
        for (Iterator<Overlay> it = mCustomOverlays.listIterator(); it.hasNext();) {
            Log.d(TAG, "Remove overlay");
            mOverlayManager.removeOverlay(it.next());
            it.remove();
        }
        if (mPointsOverlay != null) {
            mOverlayManager.removeOverlay(mPointsOverlay);
            mPointsOverlay = null;
        }
        mMapController.notifyRepaint();
    }
    public void postPoints(List<JSONNavigationItem> items) {
        Log.d(TAG, "Build route");
        mRouteId = "";
        clearOverlays();
        String jQuery = JSONRequestBuilder.buildPointsList(items);
        mJSONRouterProxy.postPoints(jQuery);
        for (JSONNavigationItem item: items) {
            Drawable overlayItemDrawable;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                overlayItemDrawable = getResources().getDrawable(R.drawable.item, getActivity().getTheme());
            } else {
                overlayItemDrawable = getResources().getDrawable(R.drawable.item);
            }
            final OverlayItem kremlin = new OverlayItem(item.location, overlayItemDrawable);
            // Create a balloon model for the object
            BalloonItem balloonKremlin = new BalloonItem(getActivity(),kremlin.getGeoPoint());
            balloonKremlin.setText(item.title);
//        // Add the balloon model to the object
            kremlin.setBalloonItem(balloonKremlin);
            // Add the object to the layer
            Overlay overlay = new Overlay(mMapController);
            byte x = 1;
            overlay.setPriority(x);
            overlay.addOverlayItem(kremlin);
            mOverlayManager.addOverlay(overlay);
//            mCustomOverlays.add(overlay);
            mPointsOverlay = overlay;
        }
    }

    //List post finished
    @Override
    public void onRoutePostReady(String routeId) {
        mRouteId = routeId;
        mJSONRouterProxy.getRoute(routeId);
    }
    //Route building finished
    @Override
    public void onRouteGetReady(JSONRoute jRoute) {
        Log.d(TAG, "Route ready; Linestrings cnt: " + jRoute.lineStrings.size());
        drawRoute(jRoute);
    }
    public void drawRoute(JSONRoute route) {
        mCurrentRoute = route;
        for (JSONLineString lineString:route.lineStrings) {
            OverlayRect overlayRect = new OverlayRect(mMapController, lineString);
            mMapController.getOverlayManager().addOverlay(overlayRect);
            mCustomOverlays.add(overlayRect);
        }
        if (mPointsOverlay != null) {
            mMapController.getOverlayManager().removeOverlay(mPointsOverlay);
        }
        mMapController.notifyRepaint();
        if (mPointsOverlay != null) {
            mMapController.getOverlayManager().addOverlay(mPointsOverlay);
            mMapController.notifyRepaint();
        }

    }

    @Override
    public void onRouteLoadingError() {

    }
}
