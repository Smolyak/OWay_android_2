package org.oway_team.oway.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class AsyncHttpPoster {
    private final String TAG = "OWay-AsyncHttpPoster";
    AsyncHttpLoaderListener mListener;
    HttpURLConnection urlConnection = null;
    BufferedReader reader = null;
    String resultJson = "";
    String mUrl;
    String mPostContent;
    Thread mThread;
    class LoaderTask implements Runnable {
        @Override
        public void run() {
            Thread parentThread = mThread;
            String sUrl = mUrl;
            String postContent = mPostContent;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                //Just ignore
                Log.w(TAG,"Thread was interrupted: "+sUrl);
                return;
            }
            if (parentThread.isInterrupted()) {
                Log.w(TAG, "Thread was interrupted: "+sUrl);
                return;
            }
            Log.d(TAG, "Begin posting to: " + mUrl);
            try {
//                sun.net.www.http.HttpClient =
                URL url = new URL(sUrl);
                Log.d(TAG, "Url: "+sUrl.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type",
                        "application/json");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                Log.d(TAG,"Geting output stream");
                OutputStream os = urlConnection.getOutputStream();

                OutputStreamWriter writer = new OutputStreamWriter(os, "UTF-8");
                writer.write(postContent);
                writer.flush();

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
                return;
            }
            mListener.onHttpPostFinished(resultJson);
            if (mThread == parentThread)
                mThread = null;
        }
    }
    public AsyncHttpPoster(AsyncHttpLoaderListener listener) {
        mListener = listener;
    }
    public void post(String url, String json) {
        mUrl = url;
        mPostContent = json;
        if (mThread != null) {
            mThread.interrupt();
            mThread = null;
        }
        mThread = new Thread(new LoaderTask());
        mThread.start();
    }

}
