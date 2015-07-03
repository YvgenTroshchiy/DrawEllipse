package com.troshchiy.drawellipse.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;

import com.troshchiy.drawellipse.util.UiUtils;

public class EllipseView extends View {

    private static final String TAG = "ee";

    private static final int TOUCH_THRESHOLD;

    private static final int CIRCLE_RADIUS = UiUtils.convertDpToPx(4);
    private static final int RADIUS_DEBUG  = UiUtils.convertDpToPx(6);

    private static final int radiusX = 400; // x width
    private static final int radiusY = 200; // y width

    private Paint paintCircle;
    private Paint paintBox;
    private RectF box;
    private final PointF center = new PointF(540, 960);

    private PointF touchDownPoint = new PointF();

    private float boundEllipseTopYLeft;
    private float boundEllipseBottomYLeft;

    private float boundEllipseTopY;
    private float boundEllipseBottomY;

    private float boundEllipseTopYRight;
    private float boundEllipseBottomYRight;

    static {
        TOUCH_THRESHOLD = UiUtils.convertDpToPx(18);
    }

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

        paintBox = new Paint();
        paintBox.setColor(Color.GRAY);

        paintCircle = new Paint();
        paintCircle.setAntiAlias(true);
        paintCircle.setColor(Color.BLACK);

        box = new RectF(center.x - radiusX, center.y - radiusY, center.x + radiusX, center.y + radiusY);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint paint = new Paint();
        paint.setTextSize(UiUtils.convertDpToPx(20));
        canvas.drawText("x: " + touchDownPoint.x + ", y: " + touchDownPoint.y, 50, 50, paint);
        canvas.drawOval(box, paintBox);

        Paint paintDebug = new Paint();
        paintDebug.setColor(Color.RED);
        canvas.drawCircle(center.x, center.y, RADIUS_DEBUG, paintDebug);

        double x;
        double y;

        for (int i = 0; i <= 360; i++) {
            x = radiusX * Math.cos(i);
            y = radiusY * Math.sin(i);
            canvas.drawCircle(box.centerX() + Math.round(x), box.centerY() + Math.round(y), CIRCLE_RADIUS, paintCircle);
        }

        showProjectionPoints(canvas);
    }

    private void showProjectionPoints(Canvas canvas) {
        canvas.drawOval(center.x - radiusX, center.y - radiusY, center.x + radiusX, center.y + radiusY, paintBox);

        Paint paint = new Paint();
        paint.setColor(Color.BLUE);

        canvas.drawCircle(touchDownPoint.x - TOUCH_THRESHOLD, boundEllipseTopYLeft, RADIUS_DEBUG, paint);
        canvas.drawCircle(touchDownPoint.x - TOUCH_THRESHOLD, boundEllipseBottomYLeft, RADIUS_DEBUG, paint);

        canvas.drawCircle(touchDownPoint.x, boundEllipseTopY, RADIUS_DEBUG, paint);
        canvas.drawCircle(touchDownPoint.x, boundEllipseBottomY, RADIUS_DEBUG, paint);

        canvas.drawCircle(touchDownPoint.x + TOUCH_THRESHOLD, boundEllipseTopYRight, RADIUS_DEBUG, paint);
        canvas.drawCircle(touchDownPoint.x + TOUCH_THRESHOLD, boundEllipseBottomYRight, RADIUS_DEBUG, paint);

        paint.setColor(Color.RED);
        canvas.drawCircle(touchDownPoint.x, touchDownPoint.y, RADIUS_DEBUG, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                touchDownPoint.x = event.getX();
                touchDownPoint.y = event.getY();

                paintCircle.setColor(
                        isTouchOnShape(touchDownPoint.x, touchDownPoint.y, true)
                                ? Color.GREEN : Color.BLACK
                );
                break;
        }

        invalidate();
        return true;
    }

    private boolean isTouchOnShape(float touchX, float touchY, boolean isTransparency) {
        boolean result = false;

        Pair<Float, Float> ellipseBoundYsLeft = getEllipseBoundYs(touchX - TOUCH_THRESHOLD);
        boundEllipseTopYLeft = ellipseBoundYsLeft.first;
        boundEllipseBottomYLeft = ellipseBoundYsLeft.second;

        Pair<Float, Float> ellipseBoundYs = getEllipseBoundYs(touchX);
        boundEllipseTopY = ellipseBoundYs.first;
        boundEllipseBottomY = ellipseBoundYs.second;

        Pair<Float, Float> ellipseBoundYsRight = getEllipseBoundYs(touchX + TOUCH_THRESHOLD);
        boundEllipseTopYRight = ellipseBoundYsRight.first;
        boundEllipseBottomYRight = ellipseBoundYsRight.second;

        if (isTransparency) {
            if (
                    (touchY >= boundEllipseTopYLeft - TOUCH_THRESHOLD && touchY <= boundEllipseTopYLeft + TOUCH_THRESHOLD)
                 || (touchY >= boundEllipseBottomYLeft - TOUCH_THRESHOLD && touchY <= boundEllipseBottomYLeft + TOUCH_THRESHOLD)

                 || (touchY >= boundEllipseTopY - TOUCH_THRESHOLD && touchY <= boundEllipseTopY + TOUCH_THRESHOLD)
                 || (touchY >= boundEllipseBottomY - TOUCH_THRESHOLD && touchY <= boundEllipseBottomY + TOUCH_THRESHOLD)

                 || (touchY >= boundEllipseTopYRight - TOUCH_THRESHOLD && touchY <= boundEllipseTopYRight + TOUCH_THRESHOLD)
                 || (touchY >= boundEllipseBottomYRight - TOUCH_THRESHOLD && touchY <= boundEllipseBottomYRight + TOUCH_THRESHOLD)
                    ) {
                result = true;
            }
        } else {
            if (
                    (touchY >= boundEllipseTopYLeft - TOUCH_THRESHOLD && touchY <= boundEllipseBottomYLeft + TOUCH_THRESHOLD)
                 || (touchY >= boundEllipseTopY - TOUCH_THRESHOLD && touchY <= boundEllipseBottomY + TOUCH_THRESHOLD)
                 || (touchY >= boundEllipseTopYRight - TOUCH_THRESHOLD && touchY <= boundEllipseBottomYRight + TOUCH_THRESHOLD)
                    ) {
                result = true;
            }
        }
        return result;
    }

    private Pair<Float, Float> getEllipseBoundYs(float touchX) {
        float x = touchX - box.centerX();

        float radiusX = box.width() / 2;
        float radiusY = box.height() / 2;

        float y = (float) (
                radiusY * Math.sqrt(
                        1 - Math.pow((x / radiusX), 2)
                )
        );

        float boundEllipseBottomY = y + box.centerY();
        float boundEllipseTopY = box.centerY() - (boundEllipseBottomY - box.centerY());

//        Log.e(TAG, "boundEllipseTopY: " + boundEllipseTopY);
//        Log.e(TAG, "boundEllipseBottomY: " + boundEllipseBottomY);

        return new Pair<>(boundEllipseTopY, boundEllipseBottomY);
    }
}