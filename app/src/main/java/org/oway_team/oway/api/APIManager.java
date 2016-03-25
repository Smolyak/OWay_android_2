package org.oway_team.oway.api;



import android.content.Context;
import android.util.Log;
import android.util.TypedValue;

import org.oway_team.oway.R;
import org.oway_team.oway.json.JSONRequestBuilder;
import org.oway_team.oway.json.NavigationItem;
import org.oway_team.oway.json.NavigationProxyListener;
import org.oway_team.oway.json.NavigationRoute;
import org.oway_team.oway.json.NavigationRouterProxy;
import org.oway_team.oway.json.SuggesterProxy;
import org.oway_team.oway.json.SuggesterProxyListener;

import java.util.ArrayList;
import java.util.List;

import ru.yandex.yandexmapkit.utils.GeoPoint;

public class APIManager {
    private final static String TAG = "OWay-ApiManager";
    private static APIManager mInstance;
    List<APIListener> mListeners;
    GeoPoint mInitialPosition;
    API mApi;

    NavigationRouterProxy mRouteLoader;
    NavigationProxyListener mRouteLoaderListener = new NavigationProxyListener() {
        @Override
        public void onRoutePostReady(String routeId) {
            if (routeId != null && !routeId.isEmpty())
                loadRoadById(routeId);
        }

        @Override
        public void onRouteGetReady(NavigationRoute route) {
            for (APIListener listener: mListeners) {
                Log.d(TAG, "Send route ready to "+listener.getClass());
                listener.onRouteReady(route);
            }
        }

        @Override
        public void onRouteLoadingError() {
            for (APIListener listener: mListeners)
                listener.onRouteLoadingFailed(APILoadingError.Unknown);
        }
    };


    SuggesterProxy mSuggesterProxy;
    SuggesterProxyListener mSuggesterProxyListener = new SuggesterProxyListener() {
        @Override
        public void onRequestFailed() {
            //No need to notify at this moment
        }

        @Override
        public void onNavigationItemsReady(List<NavigationItem> items) {
            Log.d(TAG, "Suggestions are ready: " + items.size());
            if (items == null)
                return;
            for (APIListener listener : mListeners) {
                listener.onSuggestionReady(items);
            }
        }
    };


    private APIManager() {
        mListeners = new ArrayList<APIListener>();
        mRouteLoader = new NavigationRouterProxy(mRouteLoaderListener);
        mSuggesterProxy = new SuggesterProxy(mSuggesterProxyListener);
    }
    private void loadRoadById(String routeId) {
        mRouteLoader.loadRoute(routeId);
    }

    private void notifyLoadingStart() {
        for (APIListener listener: mListeners)
            listener.onRouteLoadingStarted();
    }

    public void setContext(Context ctx) {
        //do not save ctx here
        mApi = APIBuilder.buildAPI(ctx);
        //FIXME: Novosibirsk; Hardcoded for now
        TypedValue outValue = new TypedValue();
        ctx.getResources().getValue(R.dimen.novosibirsk_lat, outValue, true);
        float lat = outValue.getFloat();
        ctx.getResources().getValue(R.dimen.novosibirsk_lat, outValue, true);
        float lon = outValue.getFloat();
        mInitialPosition = new GeoPoint(lat, lon);
    }
    public static APIManager instance() {
        if (mInstance == null) {
            mInstance = new APIManager();
        }
        return mInstance;
    }
    public void addListener(APIListener listener) {
        if (!mListeners.contains(listener)) {
            mListeners.add(listener);
        }
    }
    public void removeListener(APIListener listener) {
        if (listener != null) {
            mListeners.remove(listener);
        }
    }
    public API getApi() {
        return mApi;
    }

    /**
     * Load cached route by known id
     * Method will notify all listeners about loading start
     * @param routeId - id of route which was received from one of external sources: dialog,
     *                url, QR-code etc.
     */
    public void loadRoute(String routeId) {
        if (routeId == null || routeId.isEmpty()) {
            Log.e(TAG,"Incorrect route");
            return;
        }

        notifyLoadingStart();
        loadRoadById(routeId);
    }

    /**
     * Build route by NavigationPoints
     * Method will notify all listeners about loading start
     * @param points
     */
    public void buildRoute(List<NavigationItem> points) {
        if (points == null || points.size() < 2 ) {
            Log.w(TAG, "Cannot build route for this points list");
        }

        notifyLoadingStart();
        String jQuery = JSONRequestBuilder.buildPointsList(points);
        mRouteLoader.postPoints(jQuery);
    }

    public void loadSuggestions(String query) {
        if (query.length() >= 2 ) {
            mSuggesterProxy.getAddr(query);
        }
    }

    /**
     *  Returns the position of the city with which we work
     */
    public GeoPoint getInitialPosition() {
        return mInitialPosition;
    }
}
