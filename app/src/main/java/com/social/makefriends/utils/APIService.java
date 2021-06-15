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
                    "Authorization:key=AAAAFOohxM8:APA91bFaI7Y5zF2k-tAHZ3DxaZwk7gC5wp7X6e410TWyYz2mmXYhPpHvU4A4DgLzcbfMt6JTmUS1GH4Zo-L9WAZBR-ZUdcRahfgIYUBauJrVtlpAtTay3-n0EtlEDKLOxrA0dz8xxW3I"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification (@Body Senders body);

}