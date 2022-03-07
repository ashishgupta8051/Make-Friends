package com.social.makefriends.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.social.makefriends.R;
import com.social.makefriends.model.ConnectionModel;
import com.social.makefriends.ui.activity.account.Login;

public class InternetAlertDialog {

    public static AlertDialog internetAlertDialog(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.internet_alert_dialog,null);
        builder.setView(view);

        Button button = view.findViewById(R.id.retry_btn);

        AlertDialog alertDialog = builder.setCancelable(false).create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Toast;

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Please check your internet connection and try again.", Toast.LENGTH_SHORT).show();
            }
        });

        return alertDialog;
    }
}
