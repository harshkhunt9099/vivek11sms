package com.zone.messanger.text.Data;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zone.messanger.text.R;

import java.util.List;

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.ViewHolder> {

    private List<AppInfo> appList;
    private Context context;
    private int textColor = Color.WHITE; // Default કલર સફેદ રાખ્યો છે

    public AppAdapter(Context context, List<AppInfo> appList) {
        this.context = context;
        this.appList = appList;
    }

    // આ મેથડ એક્ટિવિટીમાંથી કલર સેટ કરવા માટે વાપરવી
    public void setTextColor(int color) {
        this.textColor = color;
        notifyDataSetChanged(); // કલર બદલાય એટલે લિસ્ટ રિફ્રેશ થશે
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        AppInfo app = appList.get(position);
//        holder.appName.setText(app.label);
//        holder.appIcon.setImageDrawable(app.icon);
//
//        // વોલપેપર મુજબનો કલર અહીં સેટ થશે
//        holder.appName.setTextColor(textColor);
//
//        // અક્ષરો વંચાય તે માટે હળવો શેડો (Shadow) આપવો
//        holder.appName.setShadowLayer(3.0f, 2.0f, 2.0f, Color.parseColor("#80000000"));
//
//        // ક્લિક કરવા પર એપ ખોલવા માટે
//        holder.itemView.setOnClickListener(v -> {
//            PackageManager pm = context.getPackageManager();
//            Intent intent = pm.getLaunchIntentForPackage(app.packageName);
//            if (intent != null) {
//                context.startActivity(intent);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return appList.size();
    }
    // જૂના લિસ્ટને નવા ફિલ્ટર કરેલા લિસ્ટ સાથે બદલવા માટે
    public void updateList(List<AppInfo> newList) {
        this.appList = newList; // અહી appList એ તમારા એડેપ્ટરનું લિસ્ટ વેરીએબલ છે
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView appIcon;
        TextView appName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            appIcon = itemView.findViewById(R.id.app_icon);
            appName = itemView.findViewById(R.id.app_name);
        }
    }
}