package com.social.friends.model;

import androidx.lifecycle.MutableLiveData;

public class ViewModel extends androidx.lifecycle.ViewModel {

    MutableLiveData<String> mutableLiveData = new MutableLiveData<>();

    public void setTxt(String s){
        mutableLiveData.setValue(s);
    }

    public MutableLiveData<String> getTxt(){
        return mutableLiveData;
    }

}
