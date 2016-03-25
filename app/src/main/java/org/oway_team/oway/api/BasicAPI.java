package org.oway_team.oway.api;


import android.content.Context;

import org.oway_team.oway.R;

public class BasicAPI extends API {
    private String mApiList;
    private String mAddressPrefix;
    private String mTaskPrefix;
    private String mAddressTaskPrefix;

    public BasicAPI(Context ctx) {
        String prefix = "http://"+ctx.getString(R.string.api_host)+"/";
        mApiList = prefix + "api/list";
        mAddressPrefix = prefix + "api/suggest/address?query=";
        mTaskPrefix = prefix + "api/suggest/keyword?query=";
        mAddressTaskPrefix = prefix + "api/suggest?query=";

    }

    @Override
    public String getApiList() {
        return mApiList;
    }

    @Override
    public String getAddressPrefix() {
        return mAddressPrefix;
    }

    @Override
    public String getTaskPrefix() {
        return mTaskPrefix;
    }

    @Override
    public String getAddressTaskPrefix() {
        return mAddressTaskPrefix;
    }
}
