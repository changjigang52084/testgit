package com.asha.vrlib;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.asha.vrlib.model.MDPinchConfig;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by hzqiujiadi on 16/5/6.
 * hzqiujiadi ashqalcn@gmail.com
 *
 * reference
 * https://github.com/boycy815/PinchImageView/blob/master/pinchimageview/src/main/java/com/boycy815/pinchimageview/PinchImageView.java
 */
public class MDTouchHelper {

    private MDVRLibrary.IAdvanceGestureListener mAdvanceGestureListener;
    private List<MDVRLibrary.IGestureListener> mClickListeners = new LinkedList<>();
    private GestureDetector mGestureDetector;
    private int mCurrentMode = 0;
    private PinchInfo mPinchInfo = new PinchInfo();
    private boolean mPinchEnabled;
    private float minScale;
    private float maxScale;
    private float mSensitivity;
    private float defaultScale;
    private float mGlobalScale;

    private static final int MODE_INIT = 0;
    private static final int MODE_PINCH = 1;

    public MDTouchHelper(Context context) {
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {

            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (mCurrentMode == MODE_PINCH) return false;

                for (MDVRLibrary.IGestureListener listener : mClickListeners){
                    listener.onClick(e);
                }
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (mCurrentMode == MODE_PINCH) return false;

                if (mAdvanceGestureListener != null)
                    mAdvanceGestureListener.onDrag(distanceX / mGlobalScale, distanceY / mGlobalScale);
                return true;
            }
        });
    }

    public boolean handleTouchEvent(MotionEvent event) {
        int action = event.getAction() & MotionEvent.ACTION_MASK;
        if(action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
            if (mCurrentMode == MODE_PINCH) {
                // end anim
            }
            mCurrentMode = MODE_INIT;
        } else if (action == MotionEvent.ACTION_POINTER_UP) {
            // one point up
            if (mCurrentMode == MODE_PINCH) {
                // more than 2 pointer
                if (event.getPointerCount() > 2) {
                    if (event.getAction() >> 8 == 0) {
                        // 0 up
                        markPinchInfo(event.getX(1), event.getY(1), event.getX(2), event.getY(2));
                    } else if (event.getAction() >> 8 == 1) {
                        // 1 up
                        markPinchInfo(event.getX(0), event.getY(0), event.getX(2), event.getY(2));
                    }
                }
            }
        } else if (action == MotionEvent.ACTION_POINTER_DOWN) {
            // >= 2 pointer
            mCurrentMode = MODE_PINCH;
            markPinchInfo(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
        } else if (action == MotionEvent.ACTION_MOVE) {
                // >= 2 pointer
                if (mCurrentMode == MODE_PINCH && event.getPointerCount() > 1) {
                    float distance = calDistance(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
                    // float[] lineCenter = MathUtils.getCenterPoint(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
                    // mLastMovePoint.set(lineCenter[0], lineCenter[1]);
                    handlePinch(distance);
                }
        }

         return mGestureDetector.onTouchEvent(event);
    }


    public void scaleTo(float scale){
        setScaleInner(mPinchInfo.setScale(scale));
    }

    public void reset(){
        setScaleInner(mPinchInfo.reset());
    }

    private void handlePinch(float distance) {
        if (mPinchEnabled){
            setScaleInner(mPinchInfo.pinch(distance));
        }
    }

    private void setScaleInner(float scale){
        if (mAdvanceGestureListener != null)
            mAdvanceGestureListener.onPinch(scale);
        mGlobalScale = scale;
    }

    private void markPinchInfo(float x1, float y1, float x2, float y2) {
        mPinchInfo.mark(x1, y1, x2, y2);
    }

    private static float calDistance(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    public void addClickListener(MDVRLibrary.IGestureListener gestureListener) {
        if (gestureListener != null) mClickListeners.add(gestureListener);
    }

    public void setAdvanceGestureListener(MDVRLibrary.IAdvanceGestureListener listener) {
        this.mAdvanceGestureListener = listener;
    }

    public void setPinchEnabled(boolean mPinchEnabled) {
        this.mPinchEnabled = mPinchEnabled;
    }

    public void setPinchConfig(MDPinchConfig pinchConfig) {
        this.minScale = pinchConfig.getMin();
        this.maxScale = pinchConfig.getMax();
        this.mSensitivity = pinchConfig.getSensitivity();
        this.defaultScale = pinchConfig.getDefaultValue();
        this.defaultScale = Math.max(minScale, this.defaultScale);
        this.defaultScale = Math.min(maxScale, this.defaultScale);
        setScaleInner(this.defaultScale);
    }

    private class PinchInfo{
        private float x1;
        private float y1;
        private float x2;
        private float y2;
        private float oDistance;
        private float prevScale;
        private float currentScale;

        public void mark(float x1, float y1, float x2, float y2){
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
            oDistance = calDistance(x1, y1, x2, y2);
            prevScale = currentScale;
        }

        public float pinch(float distance) {
            if (oDistance == 0) oDistance = distance;
            float scale = distance / oDistance - 1;
            scale *= mSensitivity;
            currentScale = prevScale + scale;
            // range
            currentScale = Math.max(currentScale, minScale);
            currentScale = Math.min(currentScale, maxScale);
            return currentScale;
        }

        public float setScale(float scale){
            prevScale = scale;
            currentScale = scale;
            return currentScale;
        }

        public float reset(){
            return setScale(defaultScale);
        }
    }
}
