package com.ethan.morephone.data.entity.message;

import com.ethan.morephone.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * Created by truongnguyen on 7/29/17.
 */
public class MessageItem implements Comparable<MessageItem> {

    @JsonProperty("sid")
    public String sid;

    @JsonProperty("date_created")
    public String dateCreated;

    @JsonProperty("date_updated")
    public String dateUpdated;

    @JsonProperty("date_sent")
    public String dateSent;

    @JsonProperty("account_sid")
    public String accountSid;

    @JsonProperty("to")
    public String to;

    @JsonProperty("from")
    public String from;

    @JsonProperty("messaging_service_sid")
    public String messagingServiceSid;

    @JsonProperty("body")
    public String body;

    @JsonProperty("status")
    public String status;

    @JsonProperty("num_segments")
    public String numSegments;

    @JsonProperty("num_media")
    public String numMedia;

    @JsonProperty("direction")
    public String direction;

    @JsonProperty("api_version")
    public String apiVersion;

    @JsonProperty("price")
    public String price;

    @JsonProperty("price_unit")
    public String priceUnit;

    @JsonProperty("error_code")
    public String errorCode;

    @JsonProperty("error_message")
    public String errorMessage;

    @JsonProperty("uri")
    public String uri;

    @JsonProperty("subresource_uris")
    public SubresourceUris subresourceUris;

    public MessageItem(String id,
                       String dateCreated,
                       String dateUpdated,
                       String dateSent,
                       String accountSid,
                       String to,
                       String from,
                       String messagingServiceSid,
                       String body,
                       String status,
                       String numSegments,
                       String numMedia,
                       String direction,
                       String apiVersion,
                       String price,
                       String priceUnit,
                       String errorCode,
                       String errorMessage,
                       String uri,
                       SubresourceUris subresourceUris
    ) {
        this.sid = id;
        this.dateCreated = dateCreated;
        this.dateUpdated = dateUpdated;
        this.dateSent = dateSent;
        this.accountSid = accountSid;
        this.to = to;
        this.from = from;
        this.messagingServiceSid = messagingServiceSid;
        this.body = body;
        this.status = status;
        this.numSegments = numSegments;
        this.numMedia = numMedia;
        this.direction = direction;
        this.apiVersion = apiVersion;
        this.price = price;
        this.priceUnit = priceUnit;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.uri = uri;
        this.subresourceUris = subresourceUris;
    }

    @Override
    public int compareTo(MessageItem messageItem) {
        Date current = DateUtils.getDate(this.dateSent);
        Date now = DateUtils.getDate(messageItem.dateSent);
        if (current != null && now != null) {
            if (current.after(now)) {
                return 1;
            } else {
                return -1;
            }
        } else {
            return 0;
        }
    }

    public class SubresourceUris {

        @JsonProperty("media")
        private String media;

        public SubresourceUris(String media) {
            this.media = media;
        }

    }
}
