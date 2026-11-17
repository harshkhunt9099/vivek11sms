package com.zone.messanger.text.Data;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.RecyclerView;

import com.zone.messanger.text.R;

import java.util.ArrayList;
import java.util.List;

public class AppGridAdapter extends RecyclerView.Adapter<AppGridAdapter.AppVH> {

    private static final int UNINSTALL_REQUEST_CODE = 1001;

    Context context;
    List<AppInfo> apps;
    private List<AppInfo> originalList;

    public AppGridAdapter(Context context, List<AppInfo> apps) {
        this.context = context;
        this.apps = apps;
        this.originalList = new ArrayList<>(apps);



    }

    @NonNull
    @Override
    public AppVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.item_app, parent, false);
        return new AppVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AppVH holder, int position) {
        AppInfo app = apps.get(position);

        if (!isAppInstalled(app.packageName)) {
            holder.itemView.setVisibility(View.GONE);
            return;
        }

        holder.itemView.setVisibility(View.VISIBLE);

        Drawable roundedIcon = getRoundedDrawable(context, app.icon, 12);
        holder.icon.setImageDrawable(roundedIcon);
        holder.name.setText(app.name);

        holder.itemView.setOnClickListener(v -> {

            Intent launch =
                    context.getPackageManager()
                            .getLaunchIntentForPackage(app.packageName);

            if (launch != null)
                context.startActivity(launch);
        });

        holder.itemView.setOnLongClickListener(v -> {
            showAppMenu(v, app);
            return true;
        });
    }

    private void showAppMenu(View anchor, AppInfo app) {

        PopupMenu popup = new PopupMenu(context, anchor);
        popup.getMenu().add("App info");
        popup.getMenu().add("Uninstall");

        popup.setOnMenuItemClickListener(item -> {

            switch (item.getTitle().toString()) {

                case "App info":
                    Intent infoIntent = new Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    infoIntent.setData(Uri.parse("package:" + app.packageName));
                    context.startActivity(infoIntent);
                    break;

                case "Uninstall":
                    try {

                        Intent intent1 = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent1.setData(Uri.parse("package:" + app.packageName));
                        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent1);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context,
                                "Unable to uninstall",
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
            }

            return true;
        });

        popup.show();
    }

    @Override
    public int getItemCount() {
        return apps.size();
    }
    public void filter(String text) {

        apps.clear();

        if (text.isEmpty()) {
            apps.addAll(originalList);
        } else {
            text = text.toLowerCase();
            for (AppInfo app : originalList) {
                if (app.name.toLowerCase().contains(text)) {
                    apps.add(app);
                }
            }
        }

        notifyDataSetChanged();
    }
    static class AppVH extends RecyclerView.ViewHolder {

        ImageView icon;
        TextView name;

        AppVH(View v) {
            super(v);
            icon = v.findViewById(R.id.appIcon);
            name = v.findViewById(R.id.appName);
        }
    }

    public Drawable getRoundedDrawable(
            Context context,
            Drawable drawable,
            float cornerRadiusDp) {

        try {

            if (drawable == null) {
                return null;
            }

            Bitmap bitmap;

            if (drawable instanceof BitmapDrawable) {

                Bitmap tempBitmap =
                        ((BitmapDrawable) drawable).getBitmap();

                if (tempBitmap == null || tempBitmap.isRecycled()) {
                    return drawable;
                }

                bitmap = tempBitmap;

            } else {

                int width = drawable.getIntrinsicWidth();
                int height = drawable.getIntrinsicHeight();

                if (width <= 0) width = 1;
                if (height <= 0) height = 1;

                bitmap = Bitmap.createBitmap(
                        width,
                        height,
                        Bitmap.Config.ARGB_8888
                );

                Canvas canvas = new Canvas(bitmap);

                drawable.setBounds(
                        0,
                        0,
                        canvas.getWidth(),
                        canvas.getHeight()
                );

                drawable.draw(canvas);
            }

            RoundedBitmapDrawable roundedDrawable =
                    RoundedBitmapDrawableFactory.create(
                            context.getResources(),
                            bitmap
                    );

            float px = cornerRadiusDp *
                    context.getResources()
                            .getDisplayMetrics().density;

            roundedDrawable.setCornerRadius(px);
            roundedDrawable.setAntiAlias(true);

            return roundedDrawable;

        } catch (Exception e) {

            e.printStackTrace();
            return drawable;
        }
    }

    private boolean isAppInstalled(String packageName) {
        try {
            context.getPackageManager()
                    .getPackageInfo(packageName, 0);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}