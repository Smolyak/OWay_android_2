package org.oway_team.oway.json;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.oway_team.oway.utils.AsyncHttpLoader;
import org.oway_team.oway.utils.AsyncHttpLoaderListener;
import org.oway_team.oway.utils.AsyncHttpPoster;


public class JSONRouterProxy implements AsyncHttpLoaderListener {
    JSONRouterProxyListener mListener;
    AsyncHttpPoster mPoster;
    AsyncHttpLoader mLoader;

    private static final String TAG = "OWay-JSONProxy";

    private static final String API_LIST_ENTRY_POINT = "http://oway.cf/api/list";
    public JSONRouterProxy(JSONRouterProxyListener listener) {
        mListener = listener;
        mPoster = new AsyncHttpPoster(this);
        mLoader = new AsyncHttpLoader(this);
    }
    public void postPoints(String jQuery) {
        mPoster.post(API_LIST_ENTRY_POINT,jQuery);
    }

    /**
     * Async call.
     * Ask server to build route
     */
    public void getRoute(String routeId) {
        String url = API_LIST_ENTRY_POINT+"/"+routeId+"/way";
        Log.d(TAG,"Trying to load: "+url);
        mLoader.load(url);
    }

    @Override
    public void onLoadingError() {
        mListener.onRouteLoadingError();
    }

    /**
     * Route in WKT recved
     * @param jsonItem
     */
    @Override
    public void onHttpGetFinished(String jsonItem) {
//        Log.d(TAG,"Http get finished: "+jsonItem);
        JSONRoute route = JSONRouteBuider.buildRoute(jsonItem);
        mListener.onRouteGetReady(route);
    }

    @Override
    public void onHttpPostFinished(String jsonItem) {
        Log.d(TAG,"HTTP POST Finished");
        String id = "";
        try {
            JSONObject jobj = new JSONObject(jsonItem);
            id = jobj.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mListener.onRoutePostReady(id);
    }
}
