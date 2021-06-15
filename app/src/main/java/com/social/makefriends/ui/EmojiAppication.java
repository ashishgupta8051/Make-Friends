package com.social.makefriends.ui;

import android.app.Application;

import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.ios.IosEmojiProvider;

public class EmojiAppication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        EmojiManager.install(new IosEmojiProvider());
    }
}
