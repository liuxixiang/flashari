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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.daimajia.numberprogressbar.NumberProgressBar;
import com.lxh.flashari.R;
import com.lxh.flashari.common.BaseCallback;
import com.lxh.flashari.common.config.Config;
import com.lxh.flashari.model.OperateWifiModel;
import com.lxh.flashari.model.OperateWifiModelImpl;
import com.lxh.flashari.model.UploadModel;
import com.lxh.flashari.model.UploadModelImpl;
import com.lxh.flashari.service.AidiCallback;
import com.lxh.flashari.utils.AidlUtils;
import com.lxh.flashari.utils.ConvertUtils;
import com.lxh.flashari.utils.FlashAirFileInfo;
import com.lxh.flashari.utils.FlashAirUploadManager;
import com.lxh.flashari.utils.ImageLoadUtils;
import com.lxh.flashari.utils.Logger;
import com.lxh.processmodule.IOperateWifiAidl;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCancellationSignal;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadOptions;

import org.json.JSONObject;
import org.qiyi.video.svg.Andromeda;

import java.util.HashMap;
import java.util.List;

public class ThumbnailAdapter extends BaseQuickAdapter<FlashAirFileInfo, BaseViewHolder> {
    private Context mContext;
    private static final String IMG_URL = "http://flashair/thumbnail.cgi?";
    private volatile boolean isCancelled = false;
    private UploadModel mUploadModel;
    private OperateWifiModel mOperateWifiModel;


    public ThumbnailAdapter(Context context, @Nullable List<FlashAirFileInfo> data) {
        super(R.layout.thumbnail_item, data);
        mContext = context;
        mUploadModel = UploadModelImpl.getInstance();
        mOperateWifiModel = OperateWifiModelImpl.getInstance();
    }

    @Override
    protected void convert(BaseViewHolder helper, final FlashAirFileInfo item) {
        item.setThumbnailUrl(IMG_URL + item.getDir() + "/" + item.getFileName());
        helper.setText(R.id.name, item.getFileName() + "");
        useGetThumbnail(item.getThumbnailUrl(), helper.getView(R.id.img),item);
        helper.getView(R.id.upload_text).setOnClickListener(v -> {
            if (item != null) {
                mOperateWifiModel.downloadFile(mContext, item.getFileName(), item.getDir(), new OperateWifiModel.OnDownOriginalImgListener() {
                    @Override
                    public void onSucceed(Bundle bundle, Bitmap bitmap) {
                        upload(item.getFileName(), bitmap, helper);
                    }

                    @Override
                    public void onFailed(Throwable throwable) {
                        showToast(throwable.getMessage() + "");
                    }
                });
            }
        });
    }


    //服务进程获取图片
    private void useGetThumbnail(String url, final ImageView imageView,FlashAirFileInfo item) {
        AidlUtils.useOperateWifiAidl(mContext, new AidiCallback<IOperateWifiAidl>() {
            @Override
            public void onSucceed(IOperateWifiAidl iOperateWifiAidl) {
                try {
                    iOperateWifiAidl.getThumbnail(url, new BaseCallback() {
                        @Override
                        public void onSucceed(Bundle bundle) {
                            if (bundle != null && bundle.containsKey(Config.KeyCode.KEY_THUMBNAIL_BITMAP)) {
                                Bitmap bitmap = bundle.getParcelable(Config.KeyCode.KEY_THUMBNAIL_BITMAP);
                                if (bitmap != null) {
                                    item.setThumbnail(bitmap);
                                    imageView.setBackground(new BitmapDrawable(mContext.getResources(), bitmap));
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

    public void upload(String fileName, Bitmap bitmap, BaseViewHolder helper) {
        byte[] picturePath = ConvertUtils.bitmap2Bytes(bitmap, Bitmap.CompressFormat.JPEG);
        HashMap<String, String> map = new HashMap<>();
        map.put("x:phone", "12345678");
        mUploadModel.uploadBytes(fileName, picturePath, (key, info, response) -> {

        }, new UploadOptions(map, null, false, new UpProgressHandler() {
            @Override
            public void progress(String key, double percent) {
                Log.e("qiniu", key + ": " + percent);
                NumberProgressBar progressBar = helper.getView(R.id.upload_progress_bar);
                TextView uploadText = helper.getView(R.id.upload_text);
                progressBar.setVisibility(View.VISIBLE);
                uploadText.setVisibility(View.GONE);
                int progress = (int) (percent * 100);
                Logger.tag("upload").e("Progress==" + progress);
                progressBar.setProgress(progress);
                if (progress == 100) {
                    progressBar.setVisibility(View.GONE);
                    uploadText.setVisibility(View.VISIBLE);
                    uploadText.setText("已上传");
                    uploadText.setEnabled(false);
                }
            }
        }, null));

    }

    public void showToast(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }
}
