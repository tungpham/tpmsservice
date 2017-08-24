package com.ethan.morephone.twilio.model;

import com.ethan.morephone.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * Created by truongnguyen on 8/24/17.
 */
public class Call implements Comparable<Call>{

    @JsonProperty("sid")
    public String sid;

    @JsonProperty("date_created")
    public String dateCreated;

    @JsonProperty("date_updated")
    public String dateUpdated;

    @JsonProperty("parent_call_sid")
    public String parentCallSid;

    @JsonProperty("account_sid")
    public String accountSid;

    @JsonProperty("to")
    public String to;

    @JsonProperty("from")
    public String from;

    @JsonProperty("to_formatted")
    public String toFormatted;

    @JsonProperty("from_formatted")
    public String fromFormatted;

    @JsonProperty("phone_number_sid")
    public String phoneNumberSid;

    @JsonProperty("status")
    public String status;

    @JsonProperty("start_time")
    public String startTime;

    @JsonProperty("end_time")
    public String endTime;

    @JsonProperty("duration")
    public String duration;

    @JsonProperty("price")
    public String price;

    @JsonProperty("price_unit")
    public String price_unit;

    @JsonProperty("direction")
    public String direction;

    @JsonProperty("answered_by")
    public String answeredBy;

    @JsonProperty("api_version")
    public String apiVersion;

    @JsonProperty("annotation")
    public String annotation;

    @JsonProperty("forwarded_from")
    public String forwardedFrom;

    @JsonProperty("group_sid")
    public String groupSid;

    @JsonProperty("caller_name")
    public String callerName;

    @JsonProperty("uri")
    public String uri;

    @JsonProperty("subresource_uris")
    public SubresourceUris subresourceUris;

    public Call(String sid, String dateCreated, String dateUpdated, String parentCallSid, String accountSid, String to, String from, String toFormatted, String fromFormatted, String phoneNumberSid, String status, String startTime, String endTime, String duration, String price, String price_unit, String direction, String answeredBy, String apiVersion, String annotation, String forwardedFrom, String groupSid, String callerName, String uri, SubresourceUris subresourceUris) {
        this.sid = sid;
        this.dateCreated = dateCreated;
        this.dateUpdated = dateUpdated;
        this.parentCallSid = parentCallSid;
        this.accountSid = accountSid;
        this.to = to;
        this.from = from;
        this.toFormatted = toFormatted;
        this.fromFormatted = fromFormatted;
        this.phoneNumberSid = phoneNumberSid;
        this.status = status;
        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = duration;
        this.price = price;
        this.price_unit = price_unit;
        this.direction = direction;
        this.answeredBy = answeredBy;
        this.apiVersion = apiVersion;
        this.annotation = annotation;
        this.forwardedFrom = forwardedFrom;
        this.groupSid = groupSid;
        this.callerName = callerName;
        this.uri = uri;
        this.subresourceUris = subresourceUris;
    }

    @Override
    public int compareTo(Call voiceItem) {
        Date current = DateUtils.getDate(this.dateCreated);
        Date now = DateUtils.getDate(voiceItem.dateCreated);
        if (current != null && now != null) {
            if (current.after(now)) {
                return -1;
            } else {
                return 1;
            }
        } else {
            return 0;
        }
    }

    public class SubresourceUris{
        @JsonProperty("notifications")
        public String notifications;

        @JsonProperty("recordings")
        public String recordings;

        public SubresourceUris(String notifications, String recordings) {
            this.notifications = notifications;
            this.recordings = recordings;
        }
    }
}
