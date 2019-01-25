package com.lxh.flashari.utils;

import android.util.Log;

import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.KeyGenerator;
import com.qiniu.android.storage.Recorder;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.persistent.FileRecorder;
import com.qiniu.android.utils.UrlSafeBase64;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class FlashAirUploadManager {
    private static final FlashAirUploadManager INSTANCE = new FlashAirUploadManager();
    private UploadManager mUploadManager;

    private FlashAirUploadManager() {
        mUploadManager = new UploadManager();

    }

    public static FlashAirUploadManager getInstance() {
        return INSTANCE;
    }

    //断点续传
    public UploadManager getUploadManager(String tempName) {
        //断点上传
        String dirPath = "/storage/emulated/0/Download";
        Recorder recorder = null;
        try {
            File f = File.createTempFile("qiniu_" + tempName, ".tmp");
            Log.d("qiniu", f.getAbsolutePath());
            dirPath = f.getParent();
            recorder = new FileRecorder(dirPath);
        } catch (Exception e) {
            e.printStackTrace();
        }

        final String dirPath1 = dirPath;
        //默认使用 key 的url_safe_base64编码字符串作为断点记录文件的文件名。
        //避免记录文件冲突（特别是key指定为null时），也可自定义文件名(下方为默认实现)：
        KeyGenerator keyGen = (key, file) -> {
            // 不必使用url_safe_base64转换，uploadManager内部会处理
            // 该返回值可替换为基于key、文件内容、上下文的其它信息生成的文件名
            String path = key + "_._" + new StringBuffer(file.getAbsolutePath()).reverse();
            Log.d("qiniu", path);
            File f = new File(dirPath1, UrlSafeBase64.encodeToString(path));
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(f));
                String tempString = null;
                int line = 1;
                try {
                    while ((tempString = reader.readLine()) != null) {
//							System.out.println("line " + line + ": " + tempString);
                        Log.d("qiniu", "line " + line + ": " + tempString);
                        line++;
                    }

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } finally {
                    try {
                        reader.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return path;
        };

        Configuration config = new Configuration.Builder()
                // recorder 分片上传时，已上传片记录器
                // keyGen 分片上传时，生成标识符，用于片记录器区分是那个文件的上传记录
                .recorder(recorder, keyGen)
                .build();
        // 实例化一个上传的实例
        mUploadManager = new UploadManager(config);
        return mUploadManager;
    }


}
