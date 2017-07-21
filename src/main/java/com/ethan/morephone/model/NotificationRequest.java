package com.ethan.morephone.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;

/**
 * Created by truongnguyen on 7/21/17.
 */
@JsonSerialize
public class NotificationRequest {

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private List<String> identity;
    private String title;
    private String body;
    private String priority;


    @JsonCreator
    public NotificationRequest(@JsonProperty("identity") List<String> identity,
                               @JsonProperty("title") String title,
                               @JsonProperty("body") String body,
                               @JsonProperty("priority") String priority) {
        this.identity = identity;
        this.title = title;
        this.body = body;
        this.priority = priority;
    }

    public List<String> getIdentity() {
        return identity;
    }

    public void setIdentity(List<String> identity) {
        this.identity = identity;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }
}
