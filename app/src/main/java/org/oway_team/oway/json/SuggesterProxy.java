package org.oway_team.oway.json;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.oway_team.oway.api.API;
import org.oway_team.oway.api.APIManager;
import org.oway_team.oway.utils.AsyncHttpLoader;
import org.oway_team.oway.utils.AsyncHttpLoaderListener;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import ru.yandex.yandexmapkit.utils.GeoPoint;

public class SuggesterProxy implements AsyncHttpLoaderListener {
    private static final String TAG = "OWay-SuggesterPxy";
    SuggesterProxyListener mListener;
    AsyncHttpLoader mLoader;
    //http://oway.cf/api/suggest/address?query=


    boolean isInProgress;
    int mCurrentTask;
    int TASK_JOB = 0;
    int TASK_ADDR = 1;
    int TASK_JOB_AND_ADDR = 2;
    /** Api calls
        api/suggest/address?query=......... // адрес
        api/suggest/keyword?query=......... // ключевое слово фирмы + рубрики
        api/suggest?query=......... // конкатенация 2х выше описанных запросов
    **/
    public SuggesterProxy(SuggesterProxyListener listener) {
        mListener = listener;
        isInProgress = false;
        mLoader = new AsyncHttpLoader(this);

    }

    public void getAddr(String addr) {
        mCurrentTask = TASK_ADDR;
        try {
            mLoader.load(APIManager.instance().getApi().getAddressPrefix()
                    + URLEncoder.encode(addr, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    public List<NavigationItem> parseSuggestions(String str) {
        List<NavigationItem> resList = new ArrayList<NavigationItem>();
        try {
            JSONArray arr = new JSONArray(str);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject item = arr.getJSONObject(i);
                NavigationItem navItem = new NavigationItem();
                navItem.title = item.getString("title");
                navItem.type = item.getString("type");
                JSONObject location = item.getJSONObject("location");
                double lon;
                double lat;
                if (location.isNull("lon"))
                    lon = 0.0;
                else
                    lon = location.getDouble("lon");

                if (location.isNull("lat"))
                    lat = 0.0;
                else
                    lat = location.getDouble("lat");
                navItem.location = new GeoPoint(lat, lon);
                resList.add(navItem);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return resList;
    }
    public void getTask(String task) {
//        cancelCurrentJob();
        mCurrentTask = TASK_JOB;
//        mLoader.execute(TASK_PREFIX+task);

    }

    public void getAddrAndTask(String query) {
        Log.d(TAG, "Begin task with query: "+query);
//        cancelCurrentJob();
        mCurrentTask = TASK_JOB_AND_ADDR;
//        mLoader.execute(ADDRESS_TASK_PREFIX+query);
    }


    @Override
    public void onLoadingError() {
        isInProgress = false;
    }

    @Override
    public void onHttpGetFinished(String jsonItem) {
        List<NavigationItem> items = parseSuggestions(jsonItem);
        isInProgress = false;
        mListener.onNavigationItemsReady(items);
    }

    @Override
    public void onHttpPostFinished(String jsonItem) {

    }
}