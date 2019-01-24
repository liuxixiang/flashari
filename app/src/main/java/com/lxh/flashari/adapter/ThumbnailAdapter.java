package com.lxh.flashari.adapter;

import android.content.Context;
import android.graphics.Bitmap;
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
        IBinder iBinder = Andromeda.with(mContext).getRemoteService(IOperateWifiAidl.class);
        if (null == iBinder) {
            return;
        }
        IOperateWifiAidl operateWifi = IOperateWifiAidl.Stub.asInterface(iBinder);
        if (null != operateWifi) {
            try {
                operateWifi.getThumbnail(url, new BaseCallback() {
                    @Override
                    public void onSucceed(Bundle bundle) {
                        if(bundle != null && bundle.containsKey(Config.KeyCode.KEY_THUMBNAIL_BITMAP)) {
                            Bitmap bitmap = bundle.getParcelable(Config.KeyCode.KEY_THUMBNAIL_BITMAP);
                            if(bitmap != null) {
                                imageView.setImageBitmap(bitmap);
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
    }
}
