package com.ethan.morephone.api.usage.domain;

import com.ethan.morephone.Constants;
import com.ethan.morephone.utils.Utils;
import org.springframework.data.annotation.Id;

import java.util.Date;

/**
 * Created by truongnguyen on 7/14/17.
 */
public final class Usage {

    @Id
    private String id;
    private String userId;
    private String accountSid;
    private double balance;
    private long messageIncoming;
    private long messageOutgoing;
    private long callIncoming;
    private long callOutgoing;
    private long createdAt;
    private long updatedAt;

    public Usage() {
    }

    private Usage(Builder builder) {
        this.userId = builder.userId;
        this.accountSid = builder.accountSid;
        this.balance = builder.balance;
        this.messageIncoming = builder.messageIncoming;
        this.messageOutgoing = builder.messageOutgoing;
        this.callIncoming = builder.callIncoming;
        this.callOutgoing = builder.callOutgoing;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAccountSid() {
        return accountSid;
    }

    public void setAccountSid(String accountSid) {
        this.accountSid = accountSid;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public long getMessageIncoming() {
        return messageIncoming;
    }

    public void setMessageIncoming(long messageIncoming) {
        this.messageIncoming = messageIncoming;
    }

    public long getMessageOutgoing() {
        return messageOutgoing;
    }

    public void setMessageOutgoing(long messageOutgoing) {
        this.messageOutgoing = messageOutgoing;
    }

    public long getCallIncoming() {
        return callIncoming;
    }

    public void setCallIncoming(long callIncoming) {
        this.callIncoming = callIncoming;
    }

    public long getCallOutgoing() {
        return callOutgoing;
    }

    public void setCallOutgoing(long callOutgoing) {
        this.callOutgoing = callOutgoing;
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

    public void updateMessageIncoming() {
//        this.balance = this.balance - Constants.PRICE_MESSAGE_INCOMING;
        this.messageIncoming = this.messageIncoming + 1;

        Date date = new Date();
        updatedAt = date.getTime();
    }

    public void updateMessageOutgoing() {
        this.balance = this.balance - Constants.PRICE_MESSAGE_OUTGOING;
        this.messageOutgoing = this.messageOutgoing + 1;

        Date date = new Date();
        updatedAt = date.getTime();
    }

    public void updateCallIncoming(double balance) {
        this.balance = balance;
        this.callIncoming = callIncoming + 1;

        Date date = new Date();
        updatedAt = date.getTime();
    }

    public void updateCallOutgoing(double balance) {
        this.balance = balance;
        this.callIncoming = callIncoming + 1;

        Date date = new Date();
        updatedAt = date.getTime();
    }

    public void updateBalance(double balance) {
        this.balance = balance;

        Date date = new Date();
        updatedAt = date.getTime();
    }

    @Override
    public String toString() {
        return String.format(
                "User[id=%s, balance=%s, accountSid=%s]",
                this.id,
                this.balance,
                this.accountSid
        );
    }


    public static class Builder {

        private String userId;
        private String accountSid;
        private double balance;
        private long messageIncoming;
        private long messageOutgoing;
        private long callIncoming;
        private long callOutgoing;

        private Builder() {
        }

        public Builder accountSid(String accountSid) {
            this.accountSid = accountSid;
            return this;
        }

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder balance(double balance) {
            this.balance = balance;
            return this;
        }

        public Builder messageIncoming(long messageIncoming) {
            this.messageIncoming = messageIncoming;
            return this;
        }

        public Builder messageOutgoing(long messageOutgoing) {
            this.messageOutgoing = messageOutgoing;
            return this;
        }

        public Builder callIncoming(long callIncoming) {
            this.callIncoming = callIncoming;
            return this;
        }

        public Builder callOutgoing(long callOutgoing) {
            this.callOutgoing = callOutgoing;
            return this;
        }

        public Usage build() {

            Usage build = new Usage(this);

            build.checkAccountSid(build.getAccountSid());

            return build;
        }
    }

    private void checkAccountSid(String accountSid) {
        Utils.notNull(accountSid, "accountSid cannot be null");
        Utils.notEmpty(accountSid, "accountSid cannot be empty");
    }
}
