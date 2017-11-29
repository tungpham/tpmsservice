package com.ethan.morephone.data.network;

import com.ethan.morephone.data.entity.message.MessageItem;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by truongnguyen on 7/25/17.
 */
public interface ApiPathWebhook {

    /*-----------------------------------------APPLICATIONS-----------------------------------------*/
    @POST("message")
    Call<Void> pushMessage(@Body MessageItem messageItem);

}
