package org.oway_team.oway.json;


public interface NavigationProxyListener {
    public void onRoutePostReady(String route);
    public void onRouteGetReady(NavigationRoute route);
    public void onRouteLoadingError();
}
