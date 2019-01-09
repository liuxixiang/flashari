package com.lxh.flashari.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
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
    protected void convert(BaseViewHolder helper, FlashAirFileInfo item) {
        helper.setText(R.id.name, item.mFileName +"" );
        Glide.with(mContext)
                .load(IMG_URL + item.mDir + "/" + item.mFileName).into((ImageView) helper.getView(R.id.img));


    }

}
