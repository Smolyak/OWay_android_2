package org.oway_team.oway.api;



import android.util.Log;

import org.oway_team.oway.json.JSONRequestBuilder;
import org.oway_team.oway.json.NavigationItem;
import org.oway_team.oway.json.NavigationProxyListener;
import org.oway_team.oway.json.NavigationRoute;
import org.oway_team.oway.json.NavigationRouterProxy;
import org.oway_team.oway.json.SuggesterProxy;
import org.oway_team.oway.json.SuggesterProxyListener;

import java.util.ArrayList;
import java.util.List;

public class APIManager {
    private final static String TAG = "OWay-ApiManager";
    private static APIManager mInstance;
    List<APIListener> mListeners;

    NavigationRouterProxy mRouteLoader;
    NavigationProxyListener mRouteLoaderListener = new NavigationProxyListener() {
        @Override
        public void onRoutePostReady(String routeId) {
            mRouteLoader.loadRoute(routeId);
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
            if (items == null)
                return;
            for (APIListener listener : mListeners)
                listener.onSuggestionReady(items);
        }
    };


    private APIManager() {
        mListeners = new ArrayList<APIListener>();
        mRouteLoader = new NavigationRouterProxy(mRouteLoaderListener);
        mSuggesterProxy = new SuggesterProxy(mSuggesterProxyListener);
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
    public void loadRoute(String routeId) {
        mRouteLoader.loadRoute(routeId);
    }

    public void buildRoute(List<NavigationItem> points) {
        if (points == null || points.size() < 2 ) {
            Log.w(TAG, "Cannot build route for this points list");
        }
        String jQuery = JSONRequestBuilder.buildPointsList(points);
        mRouteLoader.postPoints(jQuery);
    }
    public void loadSuggestions(String query) {
        mSuggesterProxy.getAddr(query);
    }
}
