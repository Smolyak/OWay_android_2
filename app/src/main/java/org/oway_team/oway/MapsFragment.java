package org.oway_team.oway;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.oway_team.oway.api.APIListener;
import org.oway_team.oway.api.APILoadingError;
import org.oway_team.oway.api.APIManager;
import org.oway_team.oway.json.NavigationItem;
import org.oway_team.oway.json.NavigationLineString;
import org.oway_team.oway.json.NavigationRoute;
import org.oway_team.oway.maps.OverlayRect;
import org.oway_team.oway.utils.Common;

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
import ru.yandex.yandexmapkit.utils.Utils;

public class MapsFragment extends Fragment implements APIListener {
    public static final String TAG = "OWay-Map";
    MapController mMapController;
    LinearLayout mView;
    OverlayManager mOverlayManager;
    NavigationRoute mCurrentRoute;
    MapView mMapView;
    NavigationRoute mRoute;
    List<Overlay> mCustomOverlays;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.maps_layout, container, false);
        mMapView = (MapView) view.findViewById(R.id.map);
        mMapView.showBuiltInScreenButtons(true);
        mMapController = mMapView.getMapController();
        //Start map on Novosibirsk
        mMapController.setPositionAnimationTo(new GeoPoint(55.018803, 82.933952));
        mOverlayManager = mMapController.getOverlayManager();

        mCustomOverlays = new ArrayList<Overlay>();
        APIManager.instance().addListener(this);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if(mRoute != null) {
//            //Ok; We're recved Context so we can draw route!
//            onRouteReady(mRoute);
//            mRoute = null;
//        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        APIManager.instance().removeListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
//        APIManager.instance().addListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
//        APIManager.instance().removeListener(this);
    }

    public void clearMap() {
        if (mOverlayManager == null || mMapController == null || mCustomOverlays.size() == 0)
            return;
        for (Iterator<Overlay> it = mCustomOverlays.listIterator(); it.hasNext(); ) {
            Log.d(TAG, "Remove overlay");
            mOverlayManager.removeOverlay(it.next());
            it.remove();
        }

        mMapController.notifyRepaint();
    }
    public void drawRoute(NavigationRoute route) {
        mCurrentRoute = route;
        for (NavigationLineString lineString : route.getLineStringsList()) {
            OverlayRect overlayRect = new OverlayRect(mMapController, lineString);
            mMapController.getOverlayManager().addOverlay(overlayRect);
            mCustomOverlays.add(overlayRect);
        }
        mMapController.notifyRepaint();

    }
    public void drawNavigationPoints(NavigationRoute route) {
        Log.d(TAG, "Draw nav points");
        for (NavigationItem item : route.getNavigationPoints()) {
            Drawable overlayItemDrawable = Common.getDrawable(getContext(),R.drawable.item);
            final OverlayItem pointOverlay = new OverlayItem(item.location, overlayItemDrawable);
            // Create a balloon model for the object
            BalloonItem pointBalloon = new BalloonItem(getActivity(), pointOverlay.getGeoPoint());
            pointBalloon.setText(item.title);
//        // Add the balloon model to the object
            pointOverlay.setBalloonItem(pointBalloon);
            // Add the object to the layer
            Overlay overlay = new Overlay(mMapController);
            byte x = 1;
            overlay.setPriority(x);
            overlay.addOverlayItem(pointOverlay);
            mOverlayManager.addOverlay(overlay);
            mCustomOverlays.add(overlay);
        }
    }

    @Override
    public void onRouteLoadingStarted() {

    }

    @Override
    public void onRouteReady(NavigationRoute route) {
        Log.d(TAG, "Route ready");
        if (getContext() == null) {
            Log.d(TAG, "Not attached yet!");
            mRoute = route;
        } else {
            clearMap();
            drawRoute(route);
            drawNavigationPoints(route);
        }
    }

    @Override
    public void onRouteLoadingFailed(APILoadingError error) {

    }

    @Override
    public void onSuggestionReady(List<NavigationItem> items) {

    }

    @Override
    public void onSuggestionLoadingFailed(APILoadingError error) {

    }
}

