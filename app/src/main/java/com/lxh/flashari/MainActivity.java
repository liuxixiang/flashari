package com.lxh.flashari;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lxh.flashari.adapter.ThumbnailAdapter;
import com.lxh.flashari.api.ApiManager;
import com.lxh.flashari.bean.NameValuePair;
import com.lxh.flashari.bean.NumberofItems;
import com.lxh.flashari.bean.ThumbnailBean;
import com.lxh.flashari.ui.ImageViewActivity;
import com.lxh.flashari.utils.FlashAirFileInfo;
import com.lxh.flashari.utils.FlashAirUtils;
import com.lxh.flashari.utils.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;

    private String rootDir = "DCIM";
    private String directoryName = rootDir;
    private TextView currentDirText;
    private TextView numFilesText;
    private WeakHashMap<String, Call<String>> mFileCalls = new WeakHashMap<>();
    private List<FlashAirFileInfo> mFileInfos = new ArrayList();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setTitleColor(Color.rgb(65, 183, 216));

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        listRootDirectory();
    }

    private void listRootDirectory() {
        directoryName = rootDir;
        listDirectory(directoryName);
    }

    private void getFileCount() {
        Call<String> call = ApiManager.getInstance().getWifiApiService().getImgsNum(directoryName);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response != null) {
                    String fileCount = response.body();
                    numFilesText.setText("Items Found: " + fileCount);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    private void getThumbnails(List<FlashAirFileInfo> fileInfos) {
        ThumbnailAdapter thumbnailAdapter = new ThumbnailAdapter(this,fileInfos);
        thumbnailAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                FlashAirFileInfo flashAirFileInfo = (FlashAirFileInfo) adapter.getItem(position);
                Intent mIntent = new Intent(MainActivity.this,ImageViewActivity.class);
                mIntent.putExtra("flashAirFileInfo",flashAirFileInfo);
                startActivity(mIntent);
            }
        });
        mRecyclerView.setAdapter(thumbnailAdapter);

    }

    private void getFiles(final String dir) {
        Call<String> call = ApiManager.getInstance().getWifiApiService().getListDCIM(ApiManager.BASE_WIFI_PATH + "/command.cgi?op=100&DIR=" + dir);
        mFileCalls.put(dir, call);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                mFileCalls.remove(dir);
                if (response != null) {
                    String result = response.body();
                    List<FlashAirFileInfo> fileList = FlashAirUtils.getFileList(dir, result);
                    if(fileList != null ) {

                        for (FlashAirFileInfo flashAirFileInfo : fileList) {
                            if (!TextUtils.isEmpty(flashAirFileInfo.getFileName())) {
                                if ((flashAirFileInfo.getFileName().toLowerCase(Locale.getDefault()).endsWith(".jpg"))
                                        || (flashAirFileInfo.getFileName().toLowerCase(Locale.getDefault()).endsWith(".jpeg"))) {
                                    mFileInfos.add(flashAirFileInfo);
                                }else {
                                    getFiles(dir+"/"+flashAirFileInfo.getFileName());
                                }
                            }
                        }
                    }
                }
                if(mFileCalls.size() == 0 ) {
                    getThumbnails(mFileInfos);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                mFileCalls.remove(dir);
                if(mFileCalls.size() == 0 ) {
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


}
