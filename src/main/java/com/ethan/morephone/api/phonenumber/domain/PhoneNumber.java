package com.ethan.morephone.api.phonenumber.domain;

import com.ethan.morephone.utils.Utils;
import org.springframework.data.annotation.Id;

import java.util.Date;

/**
 * Created by truongnguyen on 7/14/17.
 */
public final class PhoneNumber {

    @Id
    private String id;
    private String sid;
    private String phoneNumber;
    private String friendlyName;
    private String forwardPhoneNumber;
    private String forwardEmail;
    private boolean isForward;
    private String userId;
    private long expire;
    private boolean pool;
    private long createdAt;
    private long updatedAt;

    public PhoneNumber() {
    }

    private PhoneNumber(Builder builder) {
        this.sid = builder.sid;
        this.phoneNumber = builder.phoneNumber;
        this.friendlyName = builder.friendlyName;
        this.forwardPhoneNumber = builder.forwardPhoneNumber;
        this.forwardEmail = builder.forwardEmail;
        this.isForward = builder.isForward;
        this.userId = builder.userId;
        this.expire = builder.expire;
        this.pool = builder.pool;
        Date date = new Date();
        createdAt = date.getTime();
        updatedAt = date.getTime();
    }

    public static Builder getBuilder() {
        return new Builder();
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

    public boolean isForward() {
        return isForward;
    }

    public void setForward(boolean forward) {
        isForward = forward;
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

    public void enableForward(boolean isForward){
        this.isForward = isForward;
        Date date = new Date();
        updatedAt = date.getTime();
    }

    public void updateForward(String forwardPhoneNumber, String forwardEmail) {
        this.forwardPhoneNumber = forwardPhoneNumber;
        this.forwardEmail = forwardEmail;
        Date date = new Date();
        updatedAt = date.getTime();
    }

//    public void update(String token) {
//        this.token = token;
//    }

    @Override
    public String toString() {
        return String.format(
                "User[id=%s, sid=%s, friendlyName=%s]",
                this.id,
                this.sid,
                this.friendlyName
        );
    }


    public static class Builder {
        private String sid;
        private String phoneNumber;
        private String friendlyName;
        private String userId;
        private String forwardPhoneNumber;
        private String forwardEmail;
        private long expire = 0;
        private boolean isForward;
        private boolean pool;

        private Builder() {
        }


        public Builder sid(String sid) {
            this.sid = sid;
            return this;
        }

        public Builder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder friendlyName(String friendlyName) {
            this.friendlyName = friendlyName;
            return this;
        }

        public Builder forwardPhoneNumber(String forwardPhoneNumber) {
            this.forwardPhoneNumber = forwardPhoneNumber;
            return this;
        }

        public Builder expire(long expire) {
            this.expire = expire;
            return this;
        }

        public Builder forwardEmail(String forwardEmail) {
            this.forwardEmail = forwardEmail;
            return this;
        }

        public Builder isForward(boolean isForward) {
            this.isForward = isForward;
            return this;
        }

        public Builder pool(boolean pool) {
            this.pool = pool;
            return this;
        }

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public PhoneNumber build() {

            PhoneNumber build = new PhoneNumber(this);

            build.checkSid(build.getSid());

            return build;
        }
    }

    private void checkSid(String sid) {
        Utils.notNull(sid, "sid cannot be null");
        Utils.notEmpty(sid, "sid cannot be empty");
    }
}
