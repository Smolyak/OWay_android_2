package org.oway_team.oway.api;

import android.content.Context;

public class APIBuilder {
    public static API buildAPI(Context ctx) {
        API api = new BasicAPI(ctx);
        return api;
    }
}
