package com.zone.messanger.text.Data;

import android.view.MotionEvent;
import android.view.View;






























public abstract class SwipeDetector implements View.OnTouchListener {

    private float downX, downY;
    private static final int MIN_DISTANCE = 150;

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                return true;

            case MotionEvent.ACTION_UP:
                float upX = event.getX();
                float upY = event.getY();

                float deltaX = upX - downX;
                float deltaY = upY - downY;

                // Horizontal swipe
                if (Math.abs(deltaX) > Math.abs(deltaY)) {
                    if (Math.abs(deltaX) > MIN_DISTANCE) {
                        if (deltaX > 0) {
                            onSwipeRight();
                        } else {
                            onSwipeLeft();
                        }
                        return true;
                    }
                }
                // Vertical swipe
                else {
                    if (Math.abs(deltaY) > MIN_DISTANCE) {
                        if (deltaY > 0) {
                            onSwipeDown();   // TOP → BOTTOM
                        } else {
                            onSwipeUp();     // BOTTOM → TOP
                        }
                        return true;
                    }
                }
                return false;
        }
        return false;
    }

    public abstract void onSwipeRight();
    public abstract void onSwipeLeft();
    public abstract void onSwipeUp();     // bottom → top
    public abstract void onSwipeDown();   // top → bottom
}
