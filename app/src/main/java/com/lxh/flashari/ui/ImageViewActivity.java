package com.lxh.flashari.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
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
import com.lxh.flashari.MainActivity;
import com.lxh.flashari.R;
import com.lxh.flashari.api.ApiManager;
import com.lxh.flashari.utils.FlashAirFileInfo;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImageViewActivity extends AppCompatActivity {
    ImageView imageView;
    Button backButton;
    private FlashAirFileInfo mFlashAirFileInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        Intent intent = getIntent();
        if(intent != null) {
            mFlashAirFileInfo =  intent.getParcelableExtra("flashAirFileInfo");
        }
        imageView = findViewById(R.id.imageView1);
        backButton = findViewById(R.id.button2);
        getWindow().setTitleColor(Color.rgb(65, 183, 216));
        backButton.getBackground().setColorFilter(Color.rgb(65, 183, 216), PorterDuff.Mode.SRC_IN);
        if(mFlashAirFileInfo != null) {
            String fileName = mFlashAirFileInfo.getFileName();
            String directory = mFlashAirFileInfo.getDir();
//            downloadFile(fileName, directory);
        }

        getBaidu();

    }

    void downloadFile(String downloadFile, String directory) {
        final ProgressDialog waitDialog;
        // Setting ProgressDialog
        waitDialog = new ProgressDialog(this);
        waitDialog.setMessage("Now downloading...");
        waitDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        waitDialog.show();
        // Download file

       String url =  ApiManager.BASE_WIFI_PATH + "/" + directory + "/" + downloadFile;
        Glide.with(this)
                .asBitmap()
                .load(url)
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        waitDialog.dismiss();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        waitDialog.dismiss();
                        return false;
                    }
                })
                .into(imageView);

//        Call<String> call = ApiManager.getInstance().getWifiApiService().getFile(url);
//        call.enqueue(new Callback<String>() {
//            @Override
//            public void onResponse(Call<String> call, Response<String> response) {
//                waitDialog.dismiss();
//                if (response != null) {
//                    String fileCount = response.body();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<String> call, Throwable t) {
//                waitDialog.dismiss();
//            }
//        });

    }

    private void getBaidu() {
        Call<String> call = ApiManager.getInstance().getApiService().getBaidu("http://www.baidu.com");
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response != null) {
                    Toast.makeText(ImageViewActivity.this, response.body().toString(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(ImageViewActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();

            }
        });
    }

}
