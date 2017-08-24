package com.ethan.morephone.twilio.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by truongnguyen on 8/24/17.
 */
public class Record {

    public String sid;

    @JsonProperty("account_sid")
    public String accountSid;

    @JsonProperty("call_sid")
    public String callSid;

    @JsonProperty("phone_number")
    public String phoneNumber;

    public String duration;

    @JsonProperty("date_created")
    public String dateCreated;

    @JsonProperty("api_version")
    public String apiVersion;

    @JsonProperty("date_updated")
    public String dateUpdated;

    public String status;

    public String source;

    public int channels;

    public String price;

    @JsonProperty("price_unit")
    public String priceUnit;

    public String uri;

    public Record(String sid, String accountSid, String callSid, String phoneNumber, String duration, String dateCreated, String apiVersion, String dateUpdated, String status, String source, int channels, String price, String priceUnit, String uri) {
        this.sid = sid;
        this.accountSid = accountSid;
        this.callSid = callSid;
        this.phoneNumber = phoneNumber;
        this.duration = duration;
        this.dateCreated = dateCreated;
        this.apiVersion = apiVersion;
        this.dateUpdated = dateUpdated;
        this.status = status;
        this.source = source;
        this.channels = channels;
        this.price = price;
        this.priceUnit = priceUnit;
        this.uri = uri;
    }
}
