package org.oway_team.oway.json;

import java.util.List;

public interface SuggesterProxyListener {
    public void onRequestFailed();
    public void onNavigationItemsReady(List<NavigationItem> items);
}
