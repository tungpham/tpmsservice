package com.ethan.morephone.api.messagegroup.domain;

import java.util.List;

/**
 * Created by truongnguyen on 10/6/17.
 */
public class MessageGroupDTO {

    private String id;
    private String name;
    private String groupPhone;
    private String phoneNumberId;
    private List<String> messagesSid;
    private String userId;
    private long createdAt;
    private long updatedAt;

    public MessageGroupDTO() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroupPhone() {
        return groupPhone;
    }

    public void setGroupPhone(String groupPhone) {
        this.groupPhone = groupPhone;
    }

    public String getPhoneNumberId() {
        return phoneNumberId;
    }

    public void setPhoneNumberId(String phoneNumberId) {
        this.phoneNumberId = phoneNumberId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public List<String> getMessagesSid() {
        return messagesSid;
    }

    public void setMessagesSid(List<String> messagesSid) {
        this.messagesSid = messagesSid;
    }
}
