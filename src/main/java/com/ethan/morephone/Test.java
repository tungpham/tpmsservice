package com.ethan.morephone;

import com.ethan.morephone.data.entity.application.Application;
import com.ethan.morephone.data.entity.application.Applications;
import com.ethan.morephone.data.network.ApiManager;
import com.ethan.morephone.fcm.FCM;
import com.ethan.morephone.utils.Utils;

/**
 * Created by truongnguyen on 7/25/17.
 */
public class Test {

    public static void main(String[] args) {
        modifyApplication();
    }

    private static void getApplication() {
        Applications applications = ApiManager.getApplications();
        if (applications != null && applications.applications != null) {
            for (Application application : applications.applications) {
                Utils.logMessage(application.friendly_name);
                Utils.logMessage(application.sid);
            }
        }
    }


    private static void modifyApplication() {
        ApiManager.modifyApplication(Constants.TWILIO_APPLICATION_SID,
                "https://immense-temple-84969.herokuapp.com/call/call",
                "POST",
                "https://immense-temple-84969.herokuapp.com/message/receive-message",
                "POST");
    }

    private static void sendNotification(String body) {
        //Just I am passed dummy information
        String tokenId = "fQQszTMptqc:APA91bHqlbj3XQP-Y9krSquJ4jo6Qo4_Ct3790e4VsEeyt_9swOIzXyk06A5A1ZwhM1o8a4Z5Gu0R2IEPdiYMw1PAo9Z37_-496cDEFojA13piOmJaNy8Wpps8rhb6ib02X_HdVF49qg";

        String server_key = "AAAANaqlCmY:APA91bGdQKmQNlZhqLTq31yXx36auQvc9I2xA0RB-VIgGhnN4haVdXllvWgFiRkzwJ8B_qVZ8eaJbqCTr-pqlKxbq0O4hWAcUpVga655rByPKOVSB0YnoA5t08DpiNG6uj-iAArs2bCv";

//Method to send Push Notification
        FCM.send_FCM_Notification(tokenId, server_key, body);
    }
}
