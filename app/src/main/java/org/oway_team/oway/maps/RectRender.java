package org.oway_team.oway.maps;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Xfermode;
import android.util.Log;

import ru.yandex.yandexmapkit.overlay.IRender;
import ru.yandex.yandexmapkit.overlay.OverlayItem;
import ru.yandex.yandexmapkit.utils.ScreenPoint;

public class RectRender implements IRender {

    @Override
    public void draw(Canvas canvas, OverlayItem item_) {
        // TODO Auto-generated method stub

        Paint mPaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint2.setStyle(Paint.Style.STROKE);

        mPaint.setStrokeCap(Paint.Cap.BUTT);
        mPaint2.setStrokeCap(Paint.Cap.BUTT);

        mPaint.setColor(Color.parseColor("#FF4600"));
        mPaint2.setColor(Color.parseColor("#FFFFFF"));

        mPaint.setStrokeWidth(6);
        mPaint2.setStrokeWidth(14);
        OverlayRectItem item  = (OverlayRectItem)item_;
        Path p = new Path();
        if (item.screenPoint != null && item.screenPoint.size() > 0)
        {
            ScreenPoint screenPoint = item.screenPoint.get(0);
            p.moveTo(screenPoint.getX(), screenPoint.getY());

            boolean first = true;
            for(int i = 0; i < item.screenPoint.size(); i ++){
                ScreenPoint point = item.screenPoint.get(i);
                if(first){
                    first = false;
                    p.moveTo(point.getX(), point.getY());
                }

                else if(i < item.screenPoint.size() - 1){
                    ScreenPoint next = item.screenPoint.get(i + 1);
                    p.quadTo(point.getX(), point.getY(), next.getX(), next.getY());
                }
                else{
                    p.lineTo(point.getX(), point.getY());
                }
            }
            canvas.drawPath(p,mPaint2);
            canvas.drawPath(p, mPaint);

        }
    }
}
