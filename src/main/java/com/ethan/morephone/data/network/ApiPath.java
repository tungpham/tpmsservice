package com.ethan.morephone.data.network;

import com.ethan.morephone.data.entity.application.Application;
import com.ethan.morephone.data.entity.application.Applications;
import com.ethan.morephone.data.entity.phonenumbers.IncomingPhoneNumber;
import retrofit2.Call;
import retrofit2.http.*;

/**
 * Created by truongnguyen on 7/25/17.
 */
public interface ApiPath {

    /*-----------------------------------------APPLICATIONS-----------------------------------------*/

    @GET("Accounts/{accountsid}/Applications.json")
    Call<Applications> getApplications(@Path("accountsid") String accountsid);

    @DELETE("Accounts/{accountsid}/Applications/{applicationSid}.json")
    Call<Void> deleteApplications(@Path("accountsid") String accountsid, String applicationSid);

    @FormUrlEncoded
    @POST("Accounts/{accountsid}/Applications/{applicationSid}.json")
    Call<Application> modifyApplication(@Path("accountsid") String accountsid,
                                        @Path("applicationSid") String applicationSid,
                                        @Field("VoiceUrl") String voiceUrl,
                                        @Field("VoiceMethod") String voiceMethod,
                                        @Field("SmsUrl") String smsUrl,
                                        @Field("SmsMethod") String smsMethod);


    /*-----------------------------------------INCOMING PHONE NUMBERS-----------------------------------------*/
    @FormUrlEncoded
    @POST("Accounts/{accountsid}/IncomingPhoneNumbers/{incomingPhoneNumberSid}.json")
    Call<IncomingPhoneNumber> registerApplicationSms(@Path("accountsid") String accountsid,
                                                     @Path("incomingPhoneNumberSid") String incomingPhoneNumberSid,
                                                     @Field("SmsApplicationSid") String smsApplicationSid);
}
