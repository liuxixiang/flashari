package com.lxh.flashari.ui.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.lxh.flashari.adapter.ThumbnailAdapter;
import com.lxh.flashari.common.base.BaseMvpPresenter;
import com.lxh.flashari.common.config.Config;
import com.lxh.flashari.common.event.EventConstants;
import com.lxh.flashari.service.AidiCallback;
import com.lxh.flashari.ui.originalImg.OriginalImgActivity;
import com.lxh.flashari.utils.AidlUtils;
import com.lxh.flashari.utils.FlashAirFileInfo;
import com.lxh.processmodule.IOperateWifiAidl;

import org.qiyi.video.svg.Andromeda;
import org.qiyi.video.svg.event.Event;
import org.qiyi.video.svg.event.EventListener;

import java.util.List;

public class HomePresenter extends BaseMvpPresenter<HomeView> implements EventListener {

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    getThumbnails((List<FlashAirFileInfo>) msg.obj);
                    break;
            }
        }
    };

    public void onCreate() {
        Andromeda.subscribe(EventConstants.THUMBNAILS_EVENT, this);
    }

    public void getAllSDFiles() {
        AidlUtils.useOperateWifiAidl((Context) getView(), new AidiCallback<IOperateWifiAidl>() {
            @Override
            public void onSucceed(IOperateWifiAidl iOperateWifiAidl) {
                try {
                    iOperateWifiAidl.getAllSDFiles(Config.ROOT_DIR);
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
        if (EventConstants.THUMBNAILS_EVENT.equals(event.getName())) {
            Bundle bundle = event.getData();
            if (bundle != null && bundle.containsKey(Config.KeyCode.KEY_THUMBNAILS)) {
                List<FlashAirFileInfo> fileInfos = bundle.getParcelableArrayList(Config.KeyCode.KEY_THUMBNAILS);
                Message message = new Message();
                message.what = 1;
                message.obj = fileInfos;
                mHandler.sendMessage(message);
            }
        }
    }

    private void getThumbnails(List<FlashAirFileInfo> fileInfos) {
        ThumbnailAdapter thumbnailAdapter = new ThumbnailAdapter((Context) getView(), fileInfos);
        thumbnailAdapter.setOnItemClickListener((adapter, view, position) -> {
            FlashAirFileInfo flashAirFileInfo = (FlashAirFileInfo) adapter.getItem(position);
            Intent mIntent = new Intent((Context) getView(), OriginalImgActivity.class);
            mIntent.putExtra(Config.KeyCode.KEY_FILE_INFO, flashAirFileInfo);
            ((Context) getView()).startActivity(mIntent);
        });
        thumbnailAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            FlashAirFileInfo flashAirFileInfo = (FlashAirFileInfo) adapter.getItem(position);
        });

        getView().setThumbnailAdapter(thumbnailAdapter);
    }
}
