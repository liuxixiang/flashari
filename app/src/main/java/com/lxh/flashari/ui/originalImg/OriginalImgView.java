package com.lxh.flashari.ui.originalImg;


import android.graphics.Bitmap;

import com.lxh.flashari.common.base.BaseMvpView;

public interface OriginalImgView extends BaseMvpView {

    void showToast(String msg);

    void setOriginalImg(Bitmap bitmap);

    void setNumProgress(int progress);
}
