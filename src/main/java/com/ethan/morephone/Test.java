package com.ethan.morephone;

import com.ethan.morephone.data.entity.application.Application;
import com.ethan.morephone.data.entity.application.Applications;
import com.ethan.morephone.data.network.ApiManager;
import com.ethan.morephone.test.ApiMorePhone;
import com.ethan.morephone.twilio.fcm.FCM;
import com.ethan.morephone.twilio.model.BindingRequest;
import com.ethan.morephone.twilio.model.Response;
import com.ethan.morephone.utils.Utils;
import com.twilio.jwt.accesstoken.AccessToken;
import com.twilio.jwt.accesstoken.VoiceGrant;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by truongnguyen on 7/25/17.
 */
public class Test {

    public static void main(String[] args) {
        modifyApplication();
//        sendNotification("", "WHY");
//        getApplication();
//        token();
//        binding();
    }

    private static void getApplication() {
        Applications applications = ApiManager.getApplications();
        if (applications != null && applications.applications != null) {
            for (Application application : applications.applications) {
                Utils.logMessage(application.friendly_name);
                Utils.logMessage(application.sid);
                Utils.logMessage(application.smsUrl);
                Utils.logMessage(application.voiceUrl);
            }
        }
    }


    private static void modifyApplication() {
        ApiManager.modifyApplication(Constants.TWILIO_APPLICATION_SID,
                "https://immense-temple-84969.herokuapp.com/api/v1/call/dial",
                "POST",
                "https://immense-temple-84969.herokuapp.com/api/v1/message/receive-message",
                "POST");
    }

    private static void sendNotification(String title, String body) {
        //Just I am passed dummy information
        String tokenId = "ehO-dAIUbKY:APA91bGYpNmSqfUMidnnx4SxBcFNoa-80r_1-URTZDaudnUvv_kXLDkQl9MYgDCNkgFnukeKdug2rcOj_LmT_-gqDT6S3ztg73HOC9F-aQW07AwfzbNdj6Ddn2g_wUqg9TXsqg_yl_Aa";

        String server_key = "AAAANaqlCmY:APA91bGdQKmQNlZhqLTq31yXx36auQvc9I2xA0RB-VIgGhnN4haVdXllvWgFiRkzwJ8B_qVZ8eaJbqCTr-pqlKxbq0O4hWAcUpVga655rByPKOVSB0YnoA5t08DpiNG6uj-iAArs2bCv";

//Method to send Push Notification
        FCM.send_FCM_Notification(tokenId, server_key, title, body);
    }

    private static void token(){
        VoiceGrant voiceGrant = new VoiceGrant();
        voiceGrant.setOutgoingApplicationSid(Constants.TWILIO_APPLICATION_SID);
        voiceGrant.setPushCredentialSid(Constants.TWILIO_PUSH_CREDENTIALS);
        AccessToken.Builder builder = new AccessToken.Builder(Constants.TWILIO_ACCOUNT_SID, Constants.TWILIO_API_KEY, Constants.TWILIO_API_SECRET);
        builder.identity("");
        builder.grant(voiceGrant);
        String token = builder.build().toJwt();
        Utils.logMessage(token);
    }

    private static void binding(){
        BindingRequest bindingRequest = new BindingRequest("truongnguyenptit@gmail.com", "", "ehO-dAIUbKY:APA91bGYpNmSqfUMidnnx4SxBcFNoa-80r_1-URTZDaudnUvv_kXLDkQl9MYgDCNkgFnukeKdug2rcOj_LmT_-gqDT6S3ztg73HOC9F-aQW07AwfzbNdj6Ddn2g_wUqg9TXsqg_yl_Aa", "fcm");
        ApiMorePhone.binding(bindingRequest, new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if(response.isSuccessful()){

                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable throwable) {

            }
        });
    }

}
