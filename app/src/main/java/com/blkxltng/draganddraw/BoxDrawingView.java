package com.blkxltng.draganddraw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by firej on 11/28/2017.
 */

public class BoxDrawingView extends View {

    private static final String TAG = "BoxDrawingView";
    private static final String PARENT_STATE_KEY = "parentStateKey";
    private static final String BOXEN_KEY = "boxenKey";

    private Box mCurrentBox;
    private List<Box> mBoxen = new ArrayList<>();
    private Paint mBoxPaint;
    private Paint mBackgroundPaint;
    private Canvas currentCanvas;
    private PointF boxOrigin;
    private float rotation;

    //Used when creating the view in code
    public BoxDrawingView(Context context) {
        this(context, null);
    }

    //Used when inflating the view from XML
    public BoxDrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);

        //Paint the boxes a nice semitransparent red (ARGB)
        mBoxPaint = new Paint();
        mBoxPaint.setColor(0x22ff0000);

        //Paint the background off-white
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(0xfff8efe0);
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable parentState = super.onSaveInstanceState();

        Bundle bundle = new Bundle();
        bundle.putParcelable(PARENT_STATE_KEY, parentState);
        bundle.putParcelableArray(BOXEN_KEY, mBoxen.toArray(new Box[mBoxen.size()]));

        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Bundle bundle = (Bundle) state;

        super.onRestoreInstanceState(bundle.getParcelable(PARENT_STATE_KEY));

        Box[] boxes = (Box[]) bundle.getParcelableArray(BOXEN_KEY);
        mBoxen = new ArrayList<>(Arrays.asList(boxes));
    }

    @Override
    protected void onDraw(Canvas canvas) {

        currentCanvas = canvas;

//        currentCanvas.save();
        //Fill the background
        currentCanvas.drawPaint(mBackgroundPaint);

        if(boxOrigin != null) {
            currentCanvas.rotate(rotation, boxOrigin.x, boxOrigin.y);
        }

        for(Box box : mBoxen) {
            float left = Math.min(box.getOrigin().x, box.getCurrent().x);
            float right = Math.max(box.getOrigin().x, box.getCurrent().x);
            float top = Math.min(box.getOrigin().y, box.getCurrent().y);
            float bottom = Math.max(box.getOrigin().y, box.getCurrent().y);

            currentCanvas.drawRect(left, top, right, bottom, mBoxPaint);
        }

//        currentCanvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        PointF current = new PointF(event.getX(), event.getY());
        String action = "";
        int actionIndex = event.getActionIndex();

        switch (event.getActionMasked()) {
            //Used for the first touch
            case MotionEvent.ACTION_DOWN:
                action = "ACTION_DOWN";
                Log.d(TAG, "Action Index: " + actionIndex);
                //Reset drawing state
                mCurrentBox = new Box(current);
                mBoxen.add(mCurrentBox);
                boxOrigin = current;
                break;
            //Used for every touch after the first
            case MotionEvent.ACTION_POINTER_DOWN:
                action = "ACTION_POINTER_DOWN";
                Log.d(TAG, "Down - Action Index: "+ actionIndex);
                if (actionIndex == 1) {
                    PointF current2 = new PointF(event.getX(actionIndex), event.getY(actionIndex));
                    if(mCurrentBox != null) {
//                        float degrees = getAngle(current2, mCurrentBox.getCurrent(), boxOrigin);
//                        currentCanvas.save();
//                        currentCanvas.rotate(degrees, boxOrigin.x, boxOrigin.y);
//                        currentCanvas.restore();
                        rotation = getAngle(current2, mCurrentBox.getCurrent(), boxOrigin);
                        invalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                action = "ACTION_MOVE";
                if(mCurrentBox != null) {
                    int pointerCount = event.getPointerCount();
                    for(int i = 0; i < pointerCount; ++i)
                    {
                        int pointerIndex = i;
                        int pointerId = event.getPointerId(pointerIndex);
                        Log.d("pointer id - move",Integer.toString(pointerId));
                        if(pointerId == 0)
                        {
                            mCurrentBox.setCurrent(current);
                            invalidate();
                        }
                        if(pointerId == 1)
                        {
                            PointF current2 = new PointF(event.getX(pointerIndex), event.getY(pointerIndex));
//                            mCurrentBox.setOrigin(current2);
//                            float degrees = getAngle(current2, mCurrentBox.getCurrent(), boxOrigin);
//                            currentCanvas.save();
//                            currentCanvas.rotate(degrees);
//                            currentCanvas.restore();
                            rotation = getAngle(current2, mCurrentBox.getCurrent(), boxOrigin);
                            invalidate();
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                action = "ACTION_UP";
                mCurrentBox = null;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                action = "ACTION_POINTER_UP";
                Log.d(TAG, "Up - Action Index: " + actionIndex);
                break;

            case MotionEvent.ACTION_CANCEL:
                action = "ACTION_CANCEL";
                mCurrentBox = null;
                break;
        }

        Log.i(TAG, action + " at x=" + current.x + ", y=" + current.y);

        return true;
    }

    public float getAngle(PointF target, PointF origin, PointF fixedPoint) {
        double tX = target.x, tY = target.y;
        double oX = origin.x, oY = origin.y;
        double fPX = fixedPoint.x, fPY = fixedPoint.y;

        double angle1 = Math.atan2(oY - fPY, oX - fPX);
        double angle2 = Math.atan2(tY - fPY, tX - fPX);

//        double angle1 = Math.atan2(255 - 433, 255 - 444);
//        double angle2 = Math.atan2(tY - 433, tX - 444);

        float degrees = (float) Math.toDegrees(angle2 - angle1);

        return degrees;
    }
}
