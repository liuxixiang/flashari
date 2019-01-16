package com.lxh.flashari.adapter;

import android.content.Context;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lxh.flashari.R;
import com.lxh.flashari.utils.FlashAirFileInfo;
import com.lxh.flashari.utils.ImageLoadUtils;

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
        ImageLoadUtils.load(helper.itemView.getContext(), item.getThumbnailUrl(), helper.getView(R.id.img));
    }

}
