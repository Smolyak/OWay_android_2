package org.oway_team.oway.utils;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AsyncHttpLoader {
    //TODO: Rewrite me
    private static final int REQUEST_TIMEOUT = 500;
    private final String TAG = "AsyncHttpLoader";
    AsyncHttpLoaderListener mListener;
    HttpURLConnection urlConnection = null;
    BufferedReader reader = null;
    String resultJson = "";
    String mUrl;
    String mRouteId;
    Thread mThread;
    class LoaderTask implements Runnable {
        @Override
        public void run() {
            Thread parentThread = mThread;
            String sUrl = mUrl;
            String sRouteId = mRouteId;
            try {
                Thread.sleep(REQUEST_TIMEOUT);
            } catch (InterruptedException e) {
                //Just ignore
                Log.w(TAG,"Thread was interrupted: "+sUrl);
                return;
            }
            if (parentThread.isInterrupted()) {
                Log.w(TAG, "Thread was interrupted: "+sUrl);
                return;
            }
            Log.d(TAG, "Begin loading: " + mUrl);
            try {
                URL url = new URL(sUrl);
                Log.d(TAG, "Url: "+sUrl.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                Log.d(TAG, "HTTP RESP CODE: "+urlConnection.getResponseCode());

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                resultJson = buffer.toString();

            } catch (Exception e) {
                mListener.onLoadingError();
                e.printStackTrace();
            }
            if (!sRouteId.isEmpty())
                resultJson = addRouteIdToJSON(resultJson, sRouteId);
            mListener.onHttpGetFinished(resultJson);
            if (mThread == parentThread)
                mThread = null;
        }
    }
    public AsyncHttpLoader(AsyncHttpLoaderListener listener) {
        mListener = listener;
    }
    public void load(String url) {
        mRouteId = "";
        mUrl = url;
        if (mThread != null) {
            mThread.interrupt();
            mThread = null;
        }
        mThread = new Thread(new LoaderTask());
        mThread.start();
    }

    /**FIXME: must be removed later**/
    public void load(String url, String routeId) {
        mUrl = url;
        mRouteId = routeId;
        if (mThread != null) {
            mThread.interrupt();
            mThread = null;
        }
        mThread = new Thread(new LoaderTask());
        mThread.start();
    }
    /**FIXME: must be removed later**/
    private String addRouteIdToJSON(String jString, String routeId) {
        try {
            JSONObject jobj = new JSONObject(jString);
            jobj.put("id",routeId);
            return jobj.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jString;
    }
}
