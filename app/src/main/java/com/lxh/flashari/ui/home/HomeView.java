package com.lxh.flashari.ui.home;


import com.lxh.flashari.adapter.ThumbnailAdapter;
import com.lxh.flashari.common.base.BaseMvpView;

public interface HomeView extends BaseMvpView {
    void setThumbnailAdapter(ThumbnailAdapter adapter);
}
