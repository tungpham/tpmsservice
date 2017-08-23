package com.ethan.morephone.api.phonenumber.domain;

/**
 * Created by truongnguyen on 7/14/17.
 */
public final class PhoneNumberDTO {

    private String id;
    private String sid;
    private String phoneNumber;
    private String friendlyName;
    private String userId;
    private String accountSid;
    private String authToken;
    private String applicationSid;
    private String forwardPhoneNumber;
    private String forwardEmail;
    private long expire;
    private boolean isForward;
    private boolean pool;
    private long createdAt;
    private long updatedAt;

    public PhoneNumberDTO() {
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
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

    public String getAccountSid() {
        return accountSid;
    }

    public void setAccountSid(String accountSid) {
        this.accountSid = accountSid;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getApplicationSid() {
        return applicationSid;
    }

    public void setApplicationSid(String applicationSid) {
        this.applicationSid = applicationSid;
    }

    public String getForwardPhoneNumber() {
        return forwardPhoneNumber;
    }

    public void setForwardPhoneNumber(String forwardPhoneNumber) {
        this.forwardPhoneNumber = forwardPhoneNumber;
    }

    public String getForwardEmail() {
        return forwardEmail;
    }

    public void setForwardEmail(String forwardEmail) {
        this.forwardEmail = forwardEmail;
    }

    public boolean isForward() {
        return isForward;
    }

    public void setForward(boolean forward) {
        isForward = forward;
    }

    public long getExpire() {
        return expire;
    }

    public void setExpire(long expire) {
        this.expire = expire;
    }

    public boolean getPool() {
        return pool;
    }

    public void setPool(boolean pool) {
        this.pool = pool;
    }

    @Override
    public String toString() {
        return String.format(
                "UserDTO[id=%s, sid=%s, friendlyName=%s]",
                this.id,
                this.sid,
                this.friendlyName
        );
    }
}
