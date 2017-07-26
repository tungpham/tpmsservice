package com.ethan.morephone.twilio.fcm;

/**
 * Created by truongnguyen on 7/25/17.
 */
public class ContentFcm {

    public String to;
    public Content notification;


    public ContentFcm(String to, Content notification) {
        this.to = to;
        this.notification = notification;
    }
}
