package com.social.makefriends.utils;

import com.social.makefriends.notification.MyResponse;
import com.social.makefriends.notification.Senders;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAA3UBsZjI:APA91bHcVKPIm1rEhIPw1R57rrBF_Auc8DGpR7ner01X3wS2Ut1H7qgF4Hk3qoybU1kAoLTelBsu96m0XZeQVCVo72_PCnHah2rXdJ1cIHWShBrnviVFBRq9Kt2IitwhKSpeIzdMxPdO"
            }
    )

    @POST("send")
    Call<MyResponse> sendNotification (@Body Senders body);

}