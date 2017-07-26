package com.ethan.morephone.twilio.fcm;


import com.ethan.morephone.Constants;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Created by truongnguyen on 7/25/17.
 */
public class FCM {
    final static private String FCM_URL = "https://fcm.googleapis.com/fcm/send";

    /**
     * Method to send push notification to Android FireBased Cloud messaging Server.
     *
     * @param tokenId    Generated and provided from Android Client Developer
     * @param server_key Key which is Generated in FCM Server
     * @param message    which contains actual information.
     */

    public static void send_FCM_Notification(String tokenId, String title, String message) {
        try {

// Create URL instance.
            URL url = new URL(FCM_URL);
// create connection.
            HttpURLConnection conn;
            conn = (HttpURLConnection) url.openConnection();
            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);
//set method as POST or GET
            conn.setRequestMethod("POST");

//pass FCM server key
            conn.setRequestProperty("Authorization", "key=" + Constants.FCM_SERVER_KEY);

//Specify Message Format
            conn.setRequestProperty("Content-Type", "application/json");

//Create JSON Object & pass value
            Gson gson = new Gson();
            Content content = new Content(title, message);
            ContentFcm contentFcm = new ContentFcm(tokenId.trim(), content);

            String infoJson = gson.toJson(contentFcm);

            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(infoJson);
            wr.flush();

            int status = 0;
            if (null != conn) {
                status = conn.getResponseCode();
            }
            if (status != 0) {

                if (status == 200) {
//SUCCESS message
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    System.out.println("Android Notification Response : " + reader.readLine());
                } else if (status == 401) {
//client side error
                    System.out.println("Notification Response : TokenId : " + tokenId + " Error occurred :");
                } else if (status == 501) {
                    //server side error
                    System.out.println("Notification Response : [ errorCode=ServerError ] TokenId : " + tokenId);
                } else if (status == 503) {
                    //server side error
                    System.out.println("Notification Response : FCM Service is Unavailable  TokenId : " + tokenId);
                }
            }
        } catch (MalformedURLException mlfexception) {
// Prototcal Error
            System.out.println("Error occurred while sending push Notification!.." + mlfexception.getMessage());
        } catch (IOException mlfexception) {
//URL problem
            System.out.println("Reading URL, Error occurred while sending push Notification!.." + mlfexception.getMessage());
        } catch (Exception exception) {
//General Error or exception.
            System.out.println("Error occurred while sending push Notification!.." + exception.getMessage());

        }

    }

}
