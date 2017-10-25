package com.ethan.morephone.data.network;

import com.ethan.morephone.data.entity.application.Application;
import com.ethan.morephone.data.entity.application.Applications;
import com.ethan.morephone.data.entity.message.MessageItem;
import com.ethan.morephone.data.entity.message.MessageListResourceResponse;
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

    @FormUrlEncoded
    @POST("Accounts/{accountsid}/Applications.json")
    Call<Application> createApplication(@Path("accountsid") String accountsid,
                                        @Field("FriendlyName") String friendlyName,
                                        @Field("VoiceUrl") String voiceUrl,
                                        @Field("VoiceMethod") String voiceMethod,
                                        @Field("SmsUrl") String smsUrl,
                                        @Field("SmsMethod") String smsMethod);


    /*-----------------------------------------INCOMING PHONE NUMBERS-----------------------------------------*/
    @FormUrlEncoded
    @POST("Accounts/{accountsid}/IncomingPhoneNumbers/{incomingPhoneNumberSid}.json")
    Call<IncomingPhoneNumber> modifyIncomingPhoneNumber(@Path("accountsid") String accountsid,
                                                        @Path("incomingPhoneNumberSid") String incomingPhoneNumberSid,
                                                        @Field("SmsApplicationSid") String smsApplicationSid,
                                                        @Field("SmsMethod") String smsMethod,
                                                        @Field("VoiceApplicationSid") String voiceApplicationSid,
                                                        @Field("VoiceMethod") String voiceMethod);

    @FormUrlEncoded
    @POST("Accounts/{accountsid}/Messages.json")
    Call<MessageItem> createMessage(@Path("accountsid") String accountsid,
                                    @Field("From") String from,
                                    @Field("To") String to,
                                    @Field("Body") String body);

    @GET("Accounts/{accountsid}/Messages.json")
    Call<MessageListResourceResponse> getAllMessageListResource(@Path("accountsid") String accountsid);

    @GET("Accounts/{accountsid}/Messages.json")
    Call<MessageListResourceResponse> getMessages(@Path("accountsid") String accountsid, @Query("To") String phoneNumberIncoming, @Query("From") String phoneNumberOutgoing);

}
