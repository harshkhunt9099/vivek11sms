package com.zone.messanger.text.Data;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zone.messanger.text.R;


import java.util.ArrayList;

public class AppsPageFragment_home extends Fragment {

    public static AppsPageFragment_home newInstance(ArrayList<AppInfo> apps) {
        AppsPageFragment_home f = new AppsPageFragment_home();
        Bundle b = new Bundle();
        b.putSerializable("apps", apps);
        f.setArguments(b);
        return f;
    }

    AppGridAdapter appGridAdapter;
    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_home_page, container, false);


        v.findViewById(R.id.clock).setOnClickListener(view -> {

            PackageManager pm = getContext().getPackageManager();
            Intent intent = pm.getLaunchIntentForPackage("com.android.deskclock");

            if (intent != null) {
                startActivity(intent);
            }
        });

        v.findViewById(R.id.calander).setOnClickListener(view -> {

            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_APP_CALENDAR);

            try {
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(getContext(),
                        "No Calendar app found",
                        Toast.LENGTH_SHORT).show();
            }
        });



        RecyclerView rv = v.findViewById(R.id.appRecycler);
        v.findViewById(R.id.widgetContainer).setVisibility(View.VISIBLE);
        rv.setLayoutManager(new GridLayoutManager(getContext(), 4));

        ArrayList<AppInfo> apps =
                (ArrayList<AppInfo>) getArguments().getSerializable("apps");
        appGridAdapter = new AppGridAdapter(getContext(), apps);
        rv.setAdapter(appGridAdapter);
        return v;
    }




}
