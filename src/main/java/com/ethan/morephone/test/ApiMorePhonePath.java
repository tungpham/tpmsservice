package com.ethan.morephone.test;

import com.ethan.morephone.twilio.model.BindingRequest;
import com.ethan.morephone.twilio.model.Response;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by truongnguyen on 7/26/17.
 */
public interface ApiMorePhonePath {

    @POST("phone/binding")
    Call<Response> binding(@Body BindingRequest bindingRequest);
}