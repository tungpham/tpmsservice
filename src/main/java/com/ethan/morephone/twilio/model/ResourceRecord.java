package com.ethan.morephone.twilio.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by truongnguyen on 9/15/17.
 */
public class ResourceRecord {

    @JsonProperty("records")
    public List<Record> records;

    @JsonProperty("firstPageUri")
    public String firstPageUri;

    @JsonProperty("nextPageUri")
    public String nextPageUri;

    @JsonProperty("previousPageUri")
    public String previousPageUri;

    @JsonProperty("uri")
    public String uri;

    @JsonProperty("pageSize")
    public int pageSize;


    public ResourceRecord(List<Record> records, String firstPageUri, String nextPageUri, String previousPageUri, String uri, int pageSize) {
        this.records = records;
        this.firstPageUri = firstPageUri;
        this.nextPageUri = nextPageUri;
        this.previousPageUri = previousPageUri;
        this.uri = uri;
        this.pageSize = pageSize;
    }
}
