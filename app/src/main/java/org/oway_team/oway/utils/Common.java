package org.oway_team.oway.utils;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;

public class Common {
    public static Drawable getDrawable(Context context, int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return context.getResources().getDrawable(id, context.getTheme());
        } else {
            return context.getResources().getDrawable(id);
        }
    }
}
