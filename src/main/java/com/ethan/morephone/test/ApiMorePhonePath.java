package com.ethan.morephone.test;

import com.ethan.morephone.api.phonenumber.domain.PhoneNumber;
import com.ethan.morephone.twilio.model.BindingRequest;
import com.ethan.morephone.twilio.model.Response;
import retrofit2.Call;
import retrofit2.http.*;

/**
 * Created by truongnguyen on 7/26/17.
 */
public interface ApiMorePhonePath {

    @POST("phone/binding")
    Call<Response> binding(@Body BindingRequest bindingRequest);

    @DELETE("phone-number/{id}")
    Call<com.ethan.morephone.http.Response<PhoneNumber>> deletePhoneNumber(@Path("id") String id,
                                                                           @Query("account_sid") String accountSid,
                                                                           @Query("auth_token") String authToken);
    @POST("scheduler/job")
    Call<String> scheduler();
}
