package com.lxh.flashari;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;
import android.util.Log;

import com.lxh.flashari.utils.Logger;

public class MyApplication extends Application {
    public static final int TRANSPORT_TYPE_CELLULAR = NetworkCapabilities.TRANSPORT_CELLULAR;
    public static final int TRANSPORT_TYPE_WIFI = NetworkCapabilities.TRANSPORT_WIFI;
    private NetworkRequest.Builder mBuilder;
    private ConnectivityManager.NetworkCallback mCallback;
    private ConnectivityManager mConnectivityManager;
    private static MyApplication myApplication = null;

    @Override
    public void onCreate() {
        super.onCreate();
        myApplication = this;
        Logger.init();
        initNetWork();
    }

    public static MyApplication getInstance() {
        return myApplication;
    }


    private void initNetWork() {


    }

    public void setNetworkState(final int transportType) {
        if (Build.VERSION.SDK_INT >= 21) {
            mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            mBuilder = new NetworkRequest.Builder();
            NetworkRequest request = mBuilder.build();
            mBuilder.addTransportType(transportType);
            mCallback = new ConnectivityManager.NetworkCallback() {
                /**
                 * Called when the framework connects and has declared a new network ready for use.
                 * This callback may be called more than once if the {@link Network} that is
                 * satisfying the request changes.
                 */
                @TargetApi(Build.VERSION_CODES.M)
                @Override
                public void onAvailable(Network network) {
                    super.onAvailable(network);
                    Log.e("test", "onAvailable---");
                    if (transportType == TRANSPORT_TYPE_WIFI) {
                        // 也可以在将来某个时间取消这个绑定网络的设置
                        if (Build.VERSION.SDK_INT >= 23) {
                            mConnectivityManager.bindProcessToNetwork(null);
                        } else {
                            ConnectivityManager.setProcessDefaultNetwork(null);
                        }
                    } else {
                        // 可以通过下面代码将app接下来的请求都绑定到这个网络下请求
                        if (Build.VERSION.SDK_INT >= 23) {
                            mConnectivityManager.bindProcessToNetwork(network);
                        } else {
                            // 23后这个方法舍弃了
                            ConnectivityManager.setProcessDefaultNetwork(network);
                        }
                    }

                    // 只要一找到符合条件的网络就注销本callback
                    // 你也可以自己进行定义注销的条件
                    mConnectivityManager.unregisterNetworkCallback(this);
                }
            };
            mConnectivityManager.registerNetworkCallback(request, mCallback);
        }
    }


}
