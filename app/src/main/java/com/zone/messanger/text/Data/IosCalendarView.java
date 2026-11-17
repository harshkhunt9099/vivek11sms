package com.zone.messanger.text.Data;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class IosCalendarView extends LinearLayout {

    private static final int COLS = 7;

    public IosCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context c) {

        setOrientation(VERTICAL);
        setPadding(dp(10), dp(8), dp(10), dp(8));
        setBackground(bg());

        Calendar now = Calendar.getInstance();

        // Convert Android day → Monday based index (0–6)
        int todayIndex = now.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY;
        if (todayIndex < 0) todayIndex += 7;

        Calendar cal = Calendar.getInstance();

        /* ===== MONTH ===== */
        TextView month = new TextView(c);
        month.setText(new SimpleDateFormat("MMMM", Locale.US)
                .format(cal.getTime()).toUpperCase());
        month.setTextColor(Color.parseColor("#FF453A"));
        month.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        month.setTypeface(Typeface.DEFAULT_BOLD);
        month.setPadding(dp(2), dp(2), 0, dp(4));
        addView(month);

        /* ===== WEEK DAYS (🔥 LOGIC HERE) ===== */
        GridLayout weekRow = new GridLayout(c);
        weekRow.setColumnCount(COLS);
        addView(weekRow);

        String[] days = {"M","T","W","T","F","S","S"};

        for (int i = 0; i < days.length; i++) {
            TextView tv = baseCell(days[i]);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 9);

            if (i <= todayIndex) {
                // ✅ Today sudhi
                tv.setTextColor(Color.parseColor("#E5E5EA"));
            } else {
                // ⛔ Future days
                tv.setTextColor(Color.parseColor("#636366"));
            }

            weekRow.addView(tv);
        }

        /* ===== DATE GRID (unchanged) ===== */
        GridLayout grid = new GridLayout(c);
        grid.setColumnCount(COLS);
        grid.setRowCount(6);
        grid.setPadding(0, dp(2), 0, 0);
        addView(grid);

        cal.set(Calendar.DAY_OF_MONTH, 1);
        int start = cal.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY;
        if (start < 0) start += 7;

        for (int i = 0; i < start; i++) {
            grid.addView(emptyCell());
        }

        int today = now.get(Calendar.DAY_OF_MONTH);
        int max = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int day = 1; day <= max; day++) {
            grid.addView(dateCell(day, today));
        }
    }

    /* ===== CELLS ===== */

    private TextView dateCell(int day, int today) {
        TextView tv = baseCell(String.valueOf(day));
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);

        if (day == today) {
            tv.setTextColor(Color.WHITE);
            tv.setBackground(todayCircle());
        } else if (day < today) {
            tv.setTextColor(Color.parseColor("#E5E5EA"));
        } else {
            tv.setTextColor(Color.parseColor("#636366"));
        }
        return tv;
    }

    private TextView baseCell(String text) {
        TextView tv = new TextView(getContext());
        tv.setText(text);
        tv.setGravity(Gravity.CENTER);

        GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
        lp.width = 0;
        lp.height = dp(20);
        lp.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        tv.setLayoutParams(lp);

        return tv;
    }

    private View emptyCell() {
        View v = new View(getContext());
        GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
        lp.width = 0;
        lp.height = dp(20);
        lp.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        v.setLayoutParams(lp);
        return v;
    }

    /* ===== DRAWABLES ===== */

    private GradientDrawable todayCircle() {
        GradientDrawable d = new GradientDrawable();
        d.setColor(Color.parseColor("#FF453A"));
        d.setShape(GradientDrawable.OVAL);
        return d;
    }

    private GradientDrawable bg() {
        GradientDrawable d = new GradientDrawable();
        d.setColor(Color.parseColor("#1C1C1E"));
        d.setCornerRadius(dp(22));
        return d;
    }

    private int dp(int v) {
        return (int) (v * getResources().getDisplayMetrics().density);
    }
}
