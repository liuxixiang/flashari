package com.lxh.flashari.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lxh.flashari.R;
import com.lxh.flashari.common.BaseCallback;
import com.lxh.flashari.common.config.Config;
import com.lxh.flashari.service.AidiCallback;
import com.lxh.flashari.utils.AidlUtils;
import com.lxh.flashari.utils.FlashAirFileInfo;
import com.lxh.flashari.utils.ImageLoadUtils;
import com.lxh.processmodule.IOperateWifiAidl;

import org.qiyi.video.svg.Andromeda;

import java.util.List;

public class ThumbnailAdapter extends BaseQuickAdapter<FlashAirFileInfo, BaseViewHolder> {
    private Context mContext;
    private static final String IMG_URL = "http://flashair/thumbnail.cgi?";


    public ThumbnailAdapter(Context context, @Nullable List<FlashAirFileInfo> data) {
        super(R.layout.thumbnail_item, data);
        mContext = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, final FlashAirFileInfo item) {
        item.setThumbnailUrl(IMG_URL + item.getDir() + "/" + item.getFileName());
        helper.setText(R.id.name, item.getFileName() + "");
        useGetThumbnail(item.getThumbnailUrl(),helper.getView(R.id.img));
    }

    //服务进程获取图片
    private void useGetThumbnail(String url, final ImageView imageView) {
        AidlUtils.useOperateWifiAidl(mContext, new AidiCallback<IOperateWifiAidl>() {
            @Override
            public void onSucceed(IOperateWifiAidl iOperateWifiAidl) {
                try {
                    iOperateWifiAidl.getThumbnail(url, new BaseCallback() {
                        @Override
                        public void onSucceed(Bundle bundle) {
                            if(bundle != null && bundle.containsKey(Config.KeyCode.KEY_THUMBNAIL_BITMAP)) {
                                Bitmap bitmap = bundle.getParcelable(Config.KeyCode.KEY_THUMBNAIL_BITMAP);
                                if(bitmap != null) {
                                    imageView.setBackground(new BitmapDrawable(mContext.getResources(),bitmap));
//                                bitmap.recycle();
                                }
                            }
                        }
                        @Override
                        public void onFailed(String bundle) {
                        }
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailed(Throwable throwable) {
//                Toast.makeText(mContext,throwable.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }
}
