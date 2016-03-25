package org.oway_team.oway;

import android.app.Application;

import org.oway_team.oway.api.APIManager;

public class OwayApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        APIManager.instance().setContext(this);
    }
}

