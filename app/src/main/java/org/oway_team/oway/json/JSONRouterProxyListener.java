package org.oway_team.oway.json;


public interface JSONRouterProxyListener {
    public void onRoutePostReady(String jRoute);
    public void onRouteGetReady(JSONRoute jRoute);
    public void onRouteLoadingError();
}
