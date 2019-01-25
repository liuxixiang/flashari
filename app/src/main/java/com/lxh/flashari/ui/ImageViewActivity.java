package com.lxh.flashari.ui;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.lxh.flashari.R;
import com.lxh.flashari.api.ApiManager;
import com.lxh.flashari.common.BaseCallback;
import com.lxh.flashari.common.config.Config;
import com.lxh.flashari.rxjava.CustomObserver;
import com.lxh.flashari.service.AidiCallback;
import com.lxh.flashari.utils.AidlUtils;
import com.lxh.flashari.utils.ConvertUtils;
import com.lxh.flashari.utils.FlashAirFileInfo;
import com.lxh.flashari.utils.FlashAirUploadManager;
import com.lxh.flashari.utils.qiniu.Auth;
import com.lxh.processmodule.IOperateWifiAidl;
import com.qiniu.android.storage.UploadOptions;

import java.util.HashMap;

public class ImageViewActivity extends AppCompatActivity {
    ImageView imageView;
    Button backButton;
    private FlashAirFileInfo mFlashAirFileInfo;
    private String fileName;
    private Bitmap bitmap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        Intent intent = getIntent();
        if (intent != null) {
            mFlashAirFileInfo = intent.getParcelableExtra(Config.KeyCode.KEY_FILE_INFO);
        }
        imageView = findViewById(R.id.imageView1);
        backButton = findViewById(R.id.button2);
        getWindow().setTitleColor(Color.rgb(65, 183, 216));
        backButton.getBackground().setColorFilter(Color.rgb(65, 183, 216), PorterDuff.Mode.SRC_IN);
        if (mFlashAirFileInfo != null) {
            fileName = mFlashAirFileInfo.getFileName();
            String directory = mFlashAirFileInfo.getDir();
            downloadFile(fileName, directory);
        }

        backButton.setOnClickListener(v -> {
            if(bitmap != null) {
                upload(fileName, ConvertUtils.bitmap2Bytes(bitmap,Bitmap.CompressFormat.JPEG));
            }
        });

        getBaidu();

    }

    public void downloadFile(String downloadFile, String directory) {
        final ProgressDialog waitDialog;
        // Setting ProgressDialog
        waitDialog = new ProgressDialog(this);
        waitDialog.setMessage("Now downloading...");
        waitDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        waitDialog.show();
        // Download file
        String url = ApiManager.BASE_WIFI_PATH + "/" + directory + "/" + downloadFile;
        AidlUtils.useOperateWifiAidl(this, new AidiCallback<IOperateWifiAidl>() {
            @Override
            public void onSucceed(IOperateWifiAidl iOperateWifiAidl) {
                try {
                    iOperateWifiAidl.getOriginalImage(url, new BaseCallback() {
                        @Override
                        public void onSucceed(Bundle bundle) {
                            waitDialog.dismiss();
                            if (bundle != null && bundle.containsKey(Config.KeyCode.KEY_ORIGINAL_IMAGE)) {
                                bitmap = bundle.getParcelable(Config.KeyCode.KEY_ORIGINAL_IMAGE);
                                if (bitmap != null) {
                                    imageView.setImageBitmap(bitmap);
                                }
                            }
                        }

                        @Override
                        public void onFailed(String bundle) {
                            waitDialog.dismiss();
                        }
                    });
                } catch (RemoteException e) {
                    Toast.makeText(ImageViewActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(Throwable throwable) {
                waitDialog.dismiss();
            }
        });


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

    private void upload(String fileName, byte[] picturePath) {
        final String token = Auth.create(Config.QINIU_ACCESS_KEY, Config.QINIU_SECRET_KEY).uploadToken(Config.BUCKET);
        HashMap<String, String> map = new HashMap<>();
        map.put("x:phone", "12345678");
        Log.d("qiniu", "click upload");
        FlashAirUploadManager.getInstance().getUploadManager(fileName).put(picturePath, fileName, token,
                (key, info, res) -> {
                    Log.i("qiniu", key + ",\r\n " + info
                            + ",\r\n " + res);

                    if (info.isOK() == true) {
                            backButton.setText(res.toString());
                    }
                }, new UploadOptions(map, null, false,
                        (key, percent) -> {
//                                Log.i("qiniu", key + ": " + percent);
//                                progressbar.setVisibility(View.VISIBLE);
//                                int progress = (int)(percent*1000);
////											Log.d("qiniu", progress+"");
//                                progressbar.setProgress(progress);
//                                if(progress==1000){
//                                    progressbar.setVisibility(View.GONE);
//                                }
                        }, null));
    }

}
