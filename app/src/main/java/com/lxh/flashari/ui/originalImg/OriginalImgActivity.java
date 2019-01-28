package com.lxh.flashari.ui.originalImg;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.lxh.flashari.R;
import com.lxh.flashari.common.base.BaseActivity;
import com.lxh.flashari.common.config.Config;
import com.lxh.flashari.utils.FlashAirFileInfo;
import com.lxh.flashari.widget.CircleProgressView;

public class OriginalImgActivity extends BaseActivity<OriginalImgView,OriginalImgPresenter> implements OriginalImgView {
    ImageView imageView;
    Button backButton;
    private FlashAirFileInfo mFlashAirFileInfo;
    private String fileName;
    private CircleProgressView progressView;
    private Bitmap bitmap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        imageView = findViewById(R.id.imageView1);
        backButton = findViewById(R.id.button2);
        progressView = findViewById(R.id.progressView);
        getWindow().setTitleColor(Color.rgb(65, 183, 216));
        backButton.getBackground().setColorFilter(Color.rgb(65, 183, 216), PorterDuff.Mode.SRC_IN);
        Intent intent = getIntent();
        if (intent != null) {
            mFlashAirFileInfo = intent.getParcelableExtra(Config.KeyCode.KEY_FILE_INFO);
        }
        if (mFlashAirFileInfo != null) {
            fileName = mFlashAirFileInfo.getFileName();
            String directory = mFlashAirFileInfo.getDir();
            bitmap = mFlashAirFileInfo.getThumbnail();
            presenter.downloadFile(fileName, directory);
        }

        if(bitmap != null) {
            imageView.setImageBitmap(bitmap);
        }

        backButton.setOnClickListener(v -> {
                presenter.upload(fileName);
        });

    }

    @NonNull
    @Override
    public OriginalImgPresenter createPresenter() {
        return new OriginalImgPresenter();
    }

    @Override
    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setOriginalImg(Bitmap bitmap) {
        if(bitmap != null && imageView != null) {
            imageView.setImageBitmap(bitmap);
        }
    }

    @Override
    public void setNumProgress(int progress) {
        if(progressView != null) {
            progressView.setProgress(progress);
            progressView.setVisibility(progress ==100 || progress== 0 ?View.GONE:View.VISIBLE);
        }
    }

}
