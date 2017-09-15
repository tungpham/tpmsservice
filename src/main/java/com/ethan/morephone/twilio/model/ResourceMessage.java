package com.ethan.morephone.twilio.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by truongnguyen on 9/15/17.
 */
public class ResourceMessage {

    @JsonProperty("records")
    public List<ConversationModel> records;

    @JsonProperty("incomingFirstPageUri")
    public String incomingFirstPageUri;

    @JsonProperty("incomingNextPageUri")
    public String incomingNextPageUri;

    @JsonProperty("incomingPreviousPageUri")
    public String incomingPreviousPageUri;

    @JsonProperty("incomingUri")
    public String incomingUri;

    @JsonProperty("outgoingFirstPageUri")
    public String outgoingFirstPageUri;

    @JsonProperty("outgoingNextPageUri")
    public String outgoingNextPageUri;

    @JsonProperty("outgoingPreviousPageUri")
    public String outgoingPreviousPageUri;

    @JsonProperty("outgoingUri")
    public String outgoingUri;

    @JsonProperty("pageSize")
    public int pageSize;


    public ResourceMessage(List<ConversationModel> records, String incomingFirstPageUri, String incomingNextPageUri, String incomingPreviousPageUri, String incomingUri, String outgoingFirstPageUri, String outgoingNextPageUri, String outgoingPreviousPageUri, String outgoingUri, int pageSize) {
        this.records = records;
        this.incomingFirstPageUri = incomingFirstPageUri;
        this.incomingNextPageUri = incomingNextPageUri;
        this.incomingPreviousPageUri = incomingPreviousPageUri;
        this.incomingUri = incomingUri;
        this.outgoingFirstPageUri = outgoingFirstPageUri;
        this.outgoingNextPageUri = outgoingNextPageUri;
        this.outgoingPreviousPageUri = outgoingPreviousPageUri;
        this.outgoingUri = outgoingUri;
        this.pageSize = pageSize;
    }
}
