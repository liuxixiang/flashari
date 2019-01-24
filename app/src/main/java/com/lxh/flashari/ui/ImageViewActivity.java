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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.lxh.flashari.R;
import com.lxh.flashari.api.ApiManager;
import com.lxh.flashari.common.BaseCallback;
import com.lxh.flashari.common.config.Config;
import com.lxh.flashari.rxjava.CustomObserver;
import com.lxh.flashari.service.AidiCallback;
import com.lxh.flashari.utils.AidlUtils;
import com.lxh.flashari.utils.FlashAirFileInfo;
import com.lxh.processmodule.IOperateWifiAidl;

public class ImageViewActivity extends AppCompatActivity {
    ImageView imageView;
    Button backButton;
    private FlashAirFileInfo mFlashAirFileInfo;

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
            String fileName = mFlashAirFileInfo.getFileName();
            String directory = mFlashAirFileInfo.getDir();
            downloadFile(fileName, directory);
        }

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
                                Bitmap bitmap = bundle.getParcelable(Config.KeyCode.KEY_ORIGINAL_IMAGE);
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

}
