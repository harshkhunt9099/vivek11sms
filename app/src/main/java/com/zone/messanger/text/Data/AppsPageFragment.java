package com.zone.messanger.text.Data;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zone.messanger.text.R;


import java.util.ArrayList;

public class AppsPageFragment extends Fragment {

    public static AppsPageFragment newInstance(ArrayList<AppInfo> apps) {
        AppsPageFragment f = new AppsPageFragment();
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

        RecyclerView rv = v.findViewById(R.id.appRecycler);
        rv.setLayoutManager(new GridLayoutManager(getContext(), 4));

        ArrayList<AppInfo> apps =
                (ArrayList<AppInfo>) getArguments().getSerializable("apps");

        appGridAdapter = new AppGridAdapter(getContext(), apps);
        rv.setAdapter(appGridAdapter);
        return v;
    }

//    @Override
//    public void onResume() {
//        appGridAdapter.notifyDataSetChanged();
//        super.onResume();
//    }
//
//    @Override
//    public void onPause() {
//        appGridAdapter.notifyDataSetChanged();
//        super.onPause();
//    }
}
