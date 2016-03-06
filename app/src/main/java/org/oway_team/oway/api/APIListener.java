package org.oway_team.oway.api;

import org.oway_team.oway.json.NavigationItem;
import org.oway_team.oway.json.NavigationRoute;

import java.util.List;

public interface APIListener {
    void onRouteLoadingStarted();
    void onRouteReady(NavigationRoute route);
    void onRouteLoadingFailed(APILoadingError error);
    void onSuggestionReady(List<NavigationItem> items);
    void onSuggestionLoadingFailed(APILoadingError error);
}
