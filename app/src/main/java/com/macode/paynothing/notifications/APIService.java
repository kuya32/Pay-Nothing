package com.macode.paynothing.notifications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAFpNDraI:APA91bGPB6zOKqHydoMs-4e1AZnzq0dm4NmlEc8a-oj37olMh6BSPDO8RKT1v3H16WWNJ_C5JdQ932LbiS9NB9GJUKI0gt0MJSB9q75DUzZPJ0pQ1mIpD24bIP1wqrjMdq9RXhsTvYep"
    })

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
