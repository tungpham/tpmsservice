package com.ethan.morephone.twilio.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by truongnguyen on 7/22/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Response {

    private String message;
    private String error;

    @JsonCreator
    public Response(@JsonProperty("message") String message, @JsonProperty("error") String error) {
        this.message = message;
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
