package com.ethan.morephone.api.messagegroup.domain;

import org.springframework.data.annotation.Id;

import java.util.Date;

/**
 * Created by truongnguyen on 10/6/17.
 */
public class MessageGroup {

    @Id
    private String id;
    private String messageSid;
    private String phoneNumberId;
    private String userId;
    private String groupId;
    private long dateSent;
    private long createdAt;
    private long updatedAt;

    public MessageGroup() {
    }

    private MessageGroup(MessageGroup.Builder builder) {
        this.id = builder.id;
        this.messageSid = builder.messageSid;
        this.dateSent = builder.dateSent;
        this.phoneNumberId = builder.phoneNumberId;
        this.userId = builder.userId;
        this.groupId = builder.groupId;
        Date date = new Date();
        createdAt = date.getTime();
        updatedAt = date.getTime();
    }

    public static MessageGroup.Builder getBuilder() {
        return new MessageGroup.Builder();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMessageSid() {
        return messageSid;
    }

    public void setMessageSid(String messageSid) {
        this.messageSid = messageSid;
    }

    public long getDateSent() {
        return dateSent;
    }

    public void setDateSent(long dateSent) {
        this.dateSent = dateSent;
    }


    public String getPhoneNumberId() {
        return phoneNumberId;
    }

    public void setPhoneNumberId(String phoneNumberId) {
        this.phoneNumberId = phoneNumberId;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    @Override
    public String toString() {
        return String.format(
                "User[id=%s, displayName=%s, email=%s]",
                this.id
        );
    }


    public static class Builder {

        private String id;
        private String messageSid;
        private long dateSent;
        private String phoneNumberId;
        private String userId;
        private String groupId;

        private Builder() {
        }


        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder messageSid(String messageSid) {
            this.messageSid = messageSid;
            return this;
        }

        public Builder dateSent(long dateSent) {
            this.dateSent = dateSent;
            return this;
        }

        public Builder phoneNumberId(String phoneNumberId) {
            this.phoneNumberId = phoneNumberId;
            return this;
        }

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder groupId(String groupId) {
            this.groupId = groupId;
            return this;
        }

        public MessageGroup build() {
            MessageGroup build = new MessageGroup(this);
            return build;
        }
    }

}
