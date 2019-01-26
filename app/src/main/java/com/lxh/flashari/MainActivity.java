package com.lxh.flashari;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.lxh.flashari.adapter.ThumbnailAdapter;
import com.lxh.flashari.common.base.BaseActivity;
import com.lxh.flashari.service.WifiService;
import com.lxh.flashari.ui.home.HomePresenter;
import com.lxh.flashari.ui.home.HomeView;

public class MainActivity extends BaseActivity<HomeView,HomePresenter> implements HomeView {

    private RecyclerView mRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        presenter.onCreate();
        getWindow().setTitleColor(Color.rgb(65, 183, 216));

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        findViewById(R.id.textView1).setOnClickListener(v -> startService(new Intent(MainActivity.this, WifiService.class)));
        findViewById(R.id.textView2).setOnClickListener(v -> {
                    presenter.getAllSDFiles();
                }
        );


    }

    @NonNull
    @Override
    public HomePresenter createPresenter() {
        return new HomePresenter();
    }


    @Override
    public void setThumbnailAdapter(ThumbnailAdapter adapter) {
        mRecyclerView.setAdapter(adapter);
    }
}