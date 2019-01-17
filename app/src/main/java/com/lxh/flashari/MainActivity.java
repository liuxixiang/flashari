package com.lxh.flashari;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.lxh.flashari.adapter.ThumbnailAdapter;
import com.lxh.flashari.api.ApiManager;
import com.lxh.flashari.common.BaseCallback;
import com.lxh.flashari.rxjava.CustomObserver;
import com.lxh.flashari.service.WifiService;
import com.lxh.flashari.ui.ImageViewActivity;
import com.lxh.flashari.utils.FlashAirFileInfo;
import com.lxh.processmodule.IOperateWifiAidl;

import org.qiyi.video.svg.Andromeda;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;

    private String rootDir = "DCIM";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setTitleColor(Color.rgb(65, 183, 216));

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        findViewById(R.id.textView1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(new Intent(MainActivity.this, WifiService.class));
            }
        });

        findViewById(R.id.textView2).setOnClickListener(v -> {
                    useBuyAppleInShop();
                }
        );

        getBaidu();

    }

    private void getThumbnails(List<FlashAirFileInfo> fileInfos) {
        ThumbnailAdapter thumbnailAdapter = new ThumbnailAdapter(this, fileInfos);
        thumbnailAdapter.setOnItemClickListener((adapter, view, position) -> {
            FlashAirFileInfo flashAirFileInfo = (FlashAirFileInfo) adapter.getItem(position);
            Intent mIntent = new Intent(MainActivity.this, ImageViewActivity.class);
            mIntent.putExtra("flashAirFileInfo", flashAirFileInfo);
            startActivity(mIntent);
        });
        mRecyclerView.setAdapter(thumbnailAdapter);

    }

    @SuppressLint("CheckResult")
    private void getBaidu() {
        ApiManager.getInstance().sendHttp(ApiManager.getInstance().getApiService().getBaidu("http://www.baidu.com"),
                new CustomObserver<String>() {
                    @Override
                    public void onNext(String s) {
                        super.onNext(s);
                        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
                    }
                });
    }


    private void useBuyAppleInShop() {
        //IBinder buyAppleBinder = Andromeda.getInstance().getRemoteService(IBuyApple.class);
        IBinder buyAppleBinder = Andromeda.with(this).getRemoteService(IOperateWifiAidl.class);
        if (null == buyAppleBinder) {
            Toast.makeText(this, "buyAppleBinder is null! May be the service has been cancelled!", Toast.LENGTH_SHORT).show();
            return;
        }
        IOperateWifiAidl operateWifi = IOperateWifiAidl.Stub.asInterface(buyAppleBinder);
        if (null != operateWifi) {
            try {
                operateWifi.getAllSDFiles(rootDir, new BaseCallback() {
                    @Override
                    public void onSucceed(Bundle var1) {
                        if(var1 != null && var1.containsKey("Thumbnails")) {
                            List<FlashAirFileInfo> fileInfos = var1.getParcelableArrayList("Thumbnails");
                            getThumbnails(fileInfos);
                        }
                    }

                    @Override
                    public void onFailed(String var1) {

                    }
                });
//                Toast.makeText(BananaActivity.this, "got remote service in other process(:banana),appleNum:" + appleNum, Toast.LENGTH_SHORT).show();

            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
        }
    }


}