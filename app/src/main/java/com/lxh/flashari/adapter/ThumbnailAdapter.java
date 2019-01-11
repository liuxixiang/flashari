package com.lxh.flashari.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lxh.flashari.R;
import com.lxh.flashari.utils.FlashAirFileInfo;

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
        Glide.with(mContext)
                .asBitmap()
                .load(item.getThumbnailUrl())
                .into((ImageView) helper.getView(R.id.img));
    }

}
