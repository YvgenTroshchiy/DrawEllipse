package com.troshchiy.drawellipse.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.troshchiy.drawellipse.util.UiUtils;

public class EllipseView extends View {

    public static final int RADIUS = UiUtils.convertDpToPx(2);
    public static final int A      = 400;
    public static final int B      = 200;

    private PointF center = new PointF(540, 960);
    private Paint paint;

    public EllipseView(Context context) {
        super(context);
        init();
    }

    public EllipseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        if (isInEditMode()) {
            return;
        }

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        double x;
        double y;

        for (int i = 0; i <= 360; i++) {
            x = A * Math.cos(i);
            y = B * Math.sin(i);
            canvas.drawCircle(center.x + Math.round(x), center.y + Math.round(y), RADIUS, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }
}