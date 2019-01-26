package com.lxh.flashari.ui.originalImg;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.lxh.flashari.R;
import com.lxh.flashari.common.base.BaseActivity;
import com.lxh.flashari.common.config.Config;
import com.lxh.flashari.utils.FlashAirFileInfo;

public class OriginalImgActivity extends BaseActivity<OriginalImgView,OriginalImgPresenter> implements OriginalImgView {
    ImageView imageView;
    Button backButton;
    private FlashAirFileInfo mFlashAirFileInfo;
    private String fileName;
    private ProgressDialog waitDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        imageView = findViewById(R.id.imageView1);
        backButton = findViewById(R.id.button2);
        getWindow().setTitleColor(Color.rgb(65, 183, 216));
        backButton.getBackground().setColorFilter(Color.rgb(65, 183, 216), PorterDuff.Mode.SRC_IN);
        Intent intent = getIntent();
        if (intent != null) {
            mFlashAirFileInfo = intent.getParcelableExtra(Config.KeyCode.KEY_FILE_INFO);
        }
        if (mFlashAirFileInfo != null) {
            fileName = mFlashAirFileInfo.getFileName();
            String directory = mFlashAirFileInfo.getDir();
            presenter.downloadFile(fileName, directory);
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
    public void showWaitDialog() {
        // Setting ProgressDialog
        if(waitDialog == null) {
            waitDialog = new ProgressDialog(this);
        }
        waitDialog.setMessage("Now downloading...");
        waitDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        waitDialog.show();
    }

    @Override
    public void waitDialogDismiss() {
        if(waitDialog != null) {
            waitDialog.dismiss();
        }
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
}
