package com.lxh.flashari;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lxh.flashari.adapter.ThumbnailAdapter;
import com.lxh.flashari.api.ApiManager;
import com.lxh.flashari.common.config.Config;
import com.lxh.flashari.common.event.EventConstants;
import com.lxh.flashari.rxjava.CustomObserver;
import com.lxh.flashari.service.AidiCallback;
import com.lxh.flashari.service.WifiService;
import com.lxh.flashari.ui.ImageViewActivity;
import com.lxh.flashari.utils.AidlUtils;
import com.lxh.flashari.utils.FlashAirFileInfo;
import com.lxh.processmodule.IOperateWifiAidl;

import org.qiyi.video.svg.Andromeda;
import org.qiyi.video.svg.event.Event;
import org.qiyi.video.svg.event.EventListener;

import java.util.List;

public class MainActivity extends AppCompatActivity implements EventListener {

    private RecyclerView mRecyclerView;

    private String rootDir = "DCIM";
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    ((TextView)findViewById(R.id.textView1)).setText("sjkdfhjjasdf");
                    getThumbnails((List<FlashAirFileInfo>) msg.obj);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Andromeda.subscribe(EventConstants.THUMBNAILS_EVENT,MainActivity.this);

        getWindow().setTitleColor(Color.rgb(65, 183, 216));

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        findViewById(R.id.textView1).setOnClickListener(v -> startService(new Intent(MainActivity.this, WifiService.class)));

        findViewById(R.id.textView2).setOnClickListener(v -> {
                    getAllSDFiles();
                }
        );


//        getAllSDFiles();

    }

    private void getThumbnails(List<FlashAirFileInfo> fileInfos) {
        ThumbnailAdapter thumbnailAdapter = new ThumbnailAdapter(this, fileInfos);
        thumbnailAdapter.setOnItemClickListener((adapter, view, position) -> {
            FlashAirFileInfo flashAirFileInfo = (FlashAirFileInfo) adapter.getItem(position);
            Intent mIntent = new Intent(MainActivity.this, ImageViewActivity.class);
            mIntent.putExtra(Config.KeyCode.KEY_FILE_INFO, flashAirFileInfo);
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


    private void getAllSDFiles() {
        AidlUtils.useOperateWifiAidl(this, new AidiCallback<IOperateWifiAidl>() {
            @Override
            public void onSucceed(IOperateWifiAidl iOperateWifiAidl) {
                try {
                    iOperateWifiAidl.getAllSDFiles(rootDir);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailed(Throwable throwable) {

            }
        });
    }


    @Override
    public void onNotify(Event event) {
        if(EventConstants.THUMBNAILS_EVENT.equals(event.getName())) {
            Bundle bundle = event.getData();
            if(bundle != null && bundle.containsKey(Config.KeyCode.KEY_THUMBNAILS)) {
                List<FlashAirFileInfo> fileInfos = bundle.getParcelableArrayList(Config.KeyCode.KEY_THUMBNAILS);
                Message message = new Message();
                message.what = 1;
                message.obj = fileInfos;
                mHandler.sendMessage(message);

            }
        }
    }
}