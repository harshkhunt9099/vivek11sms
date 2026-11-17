package com.zone.messanger.text.Data;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;

import com.zone.messanger.text.R;

import java.util.Calendar;

public class IosClockView extends View {

    private Paint bgPaint, tickPaint, hourPaint, minPaint, secPaint, centerPaint, textPaint;
    private Handler handler = new Handler(Looper.getMainLooper());

    public IosClockView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {

        // Background
        setBackgroundResource(R.drawable.clock_bg);

        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setColor(Color.WHITE);

        tickPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        tickPaint.setColor(Color.BLACK);
        tickPaint.setStrokeCap(Paint.Cap.ROUND);

        hourPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        hourPaint.setColor(Color.BLACK);
        hourPaint.setStrokeWidth(12);
        hourPaint.setStrokeCap(Paint.Cap.ROUND);

        minPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        minPaint.setColor(Color.BLACK);
        minPaint.setStrokeWidth(8);
        minPaint.setStrokeCap(Paint.Cap.ROUND);

        secPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        secPaint.setColor(Color.parseColor("#FF9500"));
        secPaint.setStrokeWidth(4);
        secPaint.setStrokeCap(Paint.Cap.ROUND);

        centerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        centerPaint.setColor(Color.parseColor("#FF9500"));

        // 🔥 Hour number paint
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setFakeBoldText(true);
        textPaint.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));

        startTicker();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int w = getWidth();
        int h = getHeight();

        float cx = w / 2f;
        float cy = h / 2f;

        float padding = 40f;
        float r = w / 2f - padding;

        // White clock face
        canvas.drawCircle(cx, cy, r, bgPaint);

        // Minute & hour ticks
        for (int i = 0; i < 60; i++) {
            float angle = (float) Math.toRadians(i * 6 - 90);
            float start = r - (i % 5 == 0 ? 20 : 10);

            tickPaint.setStrokeWidth(i % 5 == 0 ? 4 : 2);

            canvas.drawLine(
                    cx + (float) Math.cos(angle) * start,
                    cy + (float) Math.sin(angle) * start,
                    cx + (float) Math.cos(angle) * r,
                    cy + (float) Math.sin(angle) * r,
                    tickPaint
            );
        }

        // 🔥 Hour digits (1–12)
        textPaint.setTextSize(r / 4.8f);
        Paint.FontMetrics fm = textPaint.getFontMetrics();

        for (int i = 1; i <= 12; i++) {
            double angle = Math.toRadians(i * 30 - 90);

            float x = cx + (float) Math.cos(angle) * (r - 45);
            float y = cy + (float) Math.sin(angle) * (r - 45)
                    - (fm.ascent + fm.descent) / 2;

            canvas.drawText(String.valueOf(i), x, y, textPaint);
        }

        Calendar cal = Calendar.getInstance();

        drawHand(canvas,
                (cal.get(Calendar.HOUR) % 12) * 30 + cal.get(Calendar.MINUTE) / 2f,
                r * 0.45f, hourPaint);

        drawHand(canvas,
                cal.get(Calendar.MINUTE) * 6,
                r * 0.65f, minPaint);

        drawSecondHand(canvas,
                cal.get(Calendar.SECOND) * 6,
                r * 0.75f);

        // Center dot
        canvas.drawCircle(cx, cy, 8, centerPaint);
    }


    private void drawHand(Canvas c, float angle, float len, Paint p) {
        float cx = getWidth() / 2f;
        float cy = cx;
        float rad = (float) Math.toRadians(angle - 90);
        c.drawLine(cx, cy,
                cx + (float) Math.cos(rad) * len,
                cy + (float) Math.sin(rad) * len, p);
    }

    private void drawSecondHand(Canvas c, float angle, float len) {
        float cx = getWidth() / 2f;
        float cy = cx;
        float rad = (float) Math.toRadians(angle - 90);

        // forward
        c.drawLine(cx, cy,
                cx + (float) Math.cos(rad) * len,
                cy + (float) Math.sin(rad) * len, secPaint);

        // tail
        c.drawLine(cx, cy,
                cx - (float) Math.cos(rad) * 25,
                cy - (float) Math.sin(rad) * 25, secPaint);
    }

    private void startTicker() {
        handler.postDelayed(() -> {
            invalidate();
            startTicker();
        }, 1000);
    }

    private GradientDrawable createBg() {
        GradientDrawable d = new GradientDrawable();
        d.setColor(Color.parseColor("#00000000")); // iOS grey
        d.setCornerRadius(50);
        return d;
    }
}
