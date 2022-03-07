package com.social.makefriends.utils;

import android.app.AlertDialog;
import android.content.Context;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.social.makefriends.model.ConnectionModel;
import com.social.makefriends.network.CheckInternetConnections;


public class Connection {
    private static AlertDialog alertDialog;

    public static void checkInternet(Context context, LifecycleOwner lifecycleOwner){
        alertDialog = InternetAlertDialog.internetAlertDialog(context);
        CheckInternetConnections checkInternetConnections = new CheckInternetConnections(context);
        checkInternetConnections.observe(lifecycleOwner, new Observer<ConnectionModel>() {
            @Override
            public void onChanged(ConnectionModel connectionModel) {
                if (connectionModel.getIsConnected()){
                    alertDialog.dismiss();
                }else {
                    alertDialog.show();
                }
            }
        });
    }
}
