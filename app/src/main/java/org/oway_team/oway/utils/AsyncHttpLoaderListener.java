package org.oway_team.oway.utils;


public interface AsyncHttpLoaderListener {
    public void onLoadingError();
    public void onHttpGetFinished(String jsonItem);
    public void onHttpPostFinished(String jsonItem);
}
