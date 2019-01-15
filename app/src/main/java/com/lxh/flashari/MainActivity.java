package com.lxh.flashari;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.github.pwittchen.reactivenetwork.library.rx2.Connectivity;
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;
import com.lxh.flashari.adapter.ThumbnailAdapter;
import com.lxh.flashari.api.ApiManager;
import com.lxh.flashari.rxjava.CustomObserver;
import com.lxh.flashari.rxjava.RxJavaUtils;
import com.lxh.flashari.ui.ImageViewActivity;
import com.lxh.flashari.utils.FlashAirFileInfo;
import com.lxh.flashari.utils.FlashAirUtils;
import com.lxh.flashari.utils.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.WeakHashMap;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;

    private String rootDir = "DCIM";
    private String directoryName = rootDir;
    private TextView currentDirText;
    private TextView numFilesText;
    private WeakHashMap<String, Observable<String>> mFileCalls = new WeakHashMap<>();
    private List<FlashAirFileInfo> mFileInfos = new ArrayList();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setTitleColor(Color.rgb(65, 183, 216));

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));

//        findViewById(R.id.textView1).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                listRootDirectory();
//            }
//        });
//
//        findViewById(R.id.textView2).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getBaidu();
//            }
//        });
        listRootDirectory();
    }

    private void listRootDirectory() {
        directoryName = rootDir;
        listDirectory(directoryName);
    }

    private void getFileCount() {
        ApiManager.getInstance().sendHttp(ApiManager.getInstance().getWifiApiService().getImgsNum(directoryName),
                new CustomObserver<String>() {
                    @Override
                    public void onNext(String s) {
                        super.onNext(s);
                        numFilesText.setText("Items Found: " + s);
                        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
                    }
                });
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

    private void getFiles(final String dir) {
        Observable<String> observable = ApiManager.getInstance().getWifiApiService().getListDCIM(ApiManager.BASE_WIFI_PATH + "/command.cgi?op=100&DIR=" + dir);
        ApiManager.getInstance().sendHttp(observable, new CustomObserver<String>() {
            @Override
            protected void onStart() {
                super.onStart();
                mFileCalls.put(dir, observable);
            }

            @Override
            public void onNext(String s) {
                super.onNext(s);
                List<FlashAirFileInfo> fileList = FlashAirUtils.getFileList(dir, s);
                if (fileList != null) {
                    for (FlashAirFileInfo flashAirFileInfo : fileList) {
                        if (!TextUtils.isEmpty(flashAirFileInfo.getFileName())) {
                            if ((flashAirFileInfo.getFileName().toLowerCase(Locale.getDefault()).endsWith(".jpg"))
                                    || (flashAirFileInfo.getFileName().toLowerCase(Locale.getDefault()).endsWith(".jpeg"))) {
                                mFileInfos.add(flashAirFileInfo);
                            } else {
                                getFiles(dir + "/" + flashAirFileInfo.getFileName());
                            }
                        }
                    }
                }
                this.onComplete();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                this.onComplete();
            }

            @Override
            public void onComplete() {
                super.onComplete();
                mFileCalls.remove(dir);
                if (mFileCalls.size() == 0) {
                    getThumbnails(mFileInfos);
                }
            }
        });
    }

    public void listDirectory(String dir) {
        // Prepare command directory path
        currentDirText = findViewById(R.id.textView1);
        currentDirText.setText(dir + "/");
        numFilesText = findViewById(R.id.textView2);
//        getFileCount();
        getFiles(dir);
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


}