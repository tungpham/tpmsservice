package com.ethan.morephone.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by truongnguyen on 7/21/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RegisterPhoneNumberRequest {

    private String identity;
    private String endpoint;
    private String address;
    private String binding;
    private String incomingPhoneNumberSid;

    @JsonCreator
    public RegisterPhoneNumberRequest(@JsonProperty("identity") String identity,
                                      @JsonProperty("endpoint") String endpoint,
                                      @JsonProperty("address") String address,
                                      @JsonProperty("binding") String binding,
                                      @JsonProperty("incoming_phone_number_sid") String incomingPhoneNumberSid) {

        this.identity = identity;
        this.endpoint = endpoint;
        this.address = address;
        this.binding = binding;
        this.incomingPhoneNumberSid = incomingPhoneNumberSid;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBinding() {
        return binding;
    }

    public void setBinding(String binding) {
        this.binding = binding;
    }

    public String getIncomingPhoneNumberSid() {
        return incomingPhoneNumberSid;
    }

    public void setIncomingPhoneNumberSid(String incomingPhoneNumberSid) {
        this.incomingPhoneNumberSid = incomingPhoneNumberSid;
    }
}
