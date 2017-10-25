package com.ethan.morephone.data.entity.message;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by truongnguyen on 10/25/17.
 */
public class MessageListResourceResponse {

    @JsonProperty("first_page_uri")
    public String firstPageUri;

    @JsonProperty("end")
    public int end;

    @JsonProperty("previous_page_uri")
    public String previousPageUri;

    @JsonProperty("messages")
    public List<MessageItem> messages;

    @JsonProperty("uri")
    public String uri;

    @JsonProperty("page_size")
    public int pageSize;

    @JsonProperty("start")
    public int start;

    @JsonProperty("next_page_uri")
    public String nextPageUri;

    @JsonProperty("page")
    public int page;

    public MessageListResourceResponse(String firstPageUri,
                                       int end,
                                       String previousPageUri,
                                       List<MessageItem> messages,
                                       String uri,
                                       int pageSize,
                                       int start,
                                       String nextPageUri,
                                       int page) {
        this.firstPageUri = firstPageUri;
        this.end = end;
        this.previousPageUri = previousPageUri;
        this.messages = messages;
        this.uri = uri;
        this.pageSize = pageSize;
        this.start = start;
        this.nextPageUri = nextPageUri;
        this.page = page;
    }
}
