package com.social.makefriends.network;

import static com.social.makefriends.utils.Constants.MOBILE_DATA;
import static com.social.makefriends.utils.Constants.WIFI_DATA;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Build;

import androidx.lifecycle.LiveData;

import com.social.makefriends.model.ConnectionModel;

public class CheckInternetConnections extends LiveData<ConnectionModel> {
    private final Context context;

    public CheckInternetConnections(Context context) {
        this.context = context;
    }

    @Override
    protected void onActive() {
        super.onActive();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(networkReceiver, filter);
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        context.unregisterReceiver(networkReceiver);
    }

    private final BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (connectivityManager != null) {
                    NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
                    if (capabilities != null) {
                        if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                            postValue(new ConnectionModel(WIFI_DATA,true));
                        } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                            postValue(new ConnectionModel(MOBILE_DATA,true));
                        }
                    }else {
                        postValue(new ConnectionModel(0,false));
                    }
                }
            }
        }
    };

}