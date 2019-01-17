package com.lxh.flashari;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;

import com.lxh.flashari.utils.Logger;
import com.lxh.flashari.utils.ProcessUtils;

import org.qiyi.video.svg.Andromeda;

public class MyApplication extends Application {
    private static MyApplication myApplication = null;

    @Override
    public void onCreate() {
        super.onCreate();
        myApplication = this;
        Andromeda.init(this);
        Logger.init();
        initNetwork();
        Logger.tag("lxh").e("MyApplication-->onCreate(),pid:" + android.os.Process.myPid()
                + ",processName:" + ProcessUtils.getCurrentProcessName() + ",isMainProcess:" + ProcessUtils.isMainProcess());

    }

    private void initNetwork() {
        if (ProcessUtils.isMainProcess()) {
            if (Build.VERSION.SDK_INT >= 21) {
                ConnectivityManager connectivityManager = (ConnectivityManager) MyApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
                ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {

                    @TargetApi(Build.VERSION_CODES.M)
                    @Override
                    public void onAvailable(Network network) {
                        super.onAvailable(network);
                        if (Build.VERSION.SDK_INT >= 23) {
                            connectivityManager.bindProcessToNetwork(network);
                        } else {
                            ConnectivityManager.setProcessDefaultNetwork(network);
                        }
                        connectivityManager.unregisterNetworkCallback(this);
                    }
                };
                NetworkRequest.Builder builder = new NetworkRequest.Builder();
                NetworkRequest request = builder.build();

                builder.addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
                if (connectivityManager != null) {
                    connectivityManager.registerNetworkCallback(request, networkCallback);
                }
            }
        }


    }

    public static MyApplication getInstance() {
        return myApplication;
    }


}
