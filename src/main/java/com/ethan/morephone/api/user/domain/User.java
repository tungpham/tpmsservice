package com.ethan.morephone.api.user.domain;

import com.ethan.morephone.utils.Utils;
import org.springframework.data.annotation.Id;

import java.util.Date;
import java.util.List;

/**
 * Created by truongnguyen on 7/14/17.
 */
public final class User {

    @Id
    private String id;
    private String accountSid;
    private String applicationSid;
    private String email;
    private String firstName;
    private String lastName;
    private String country;
    private String languageCode;
    private String device;
    private List<TokenFcm> tokenFcms;
    private String platform;
    private long createdAt;
    private long updatedAt;

    public User() {
    }

    private User(Builder builder) {
        this.email = builder.email;
        this.accountSid = builder.accountSid;
        this.applicationSid = builder.applicationSid;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.country = builder.country;
        this.languageCode = builder.languageCode;
        this.tokenFcms = builder.tokenFcms;
        this.device = builder.device;
        this.platform = builder.platform;
        Date date = new Date();
        createdAt = date.getTime();
        updatedAt = date.getTime();
    }

    public static Builder getBuilder() {
        return new Builder();
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
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

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getPlatform() {
        return platform;
    }

    public List<TokenFcm> getTokenFcms() {
        return tokenFcms;
    }

    public void setTokenFcms(List<TokenFcm> token) {
        this.tokenFcms = token;
    }

    public String getAccountSid() {
        return accountSid;
    }

    public void setAccountSid(String accountSid) {
        this.accountSid = accountSid;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getApplicationSid() {
        return applicationSid;
    }

    public void setApplicationSid(String applicationSid) {
        this.applicationSid = applicationSid;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void update(String country, String languageCode) {
        this.country = country;
        this.languageCode = languageCode;
        Date date = new Date();
        updatedAt = date.getTime();
    }

    public void update(List<TokenFcm> token) {
        this.tokenFcms = token;
        Date date = new Date();
        updatedAt = date.getTime();
    }



    @Override
    public String toString() {
        return String.format(
                "User[id=%s, email=%s, token=%s]",
                this.id,
                this.email,
                this.tokenFcms
        );
    }


    public static class Builder {
        private String email;
        private String accountSid;
        private String applicationSid;

        private String firstName;
        private String lastName;
        private String country;
        private String languageCode;
        private List<TokenFcm> tokenFcms;
        private String device;
        private String platform;

        private Builder() {
        }


        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder accountSid(String accountSid) {
            this.accountSid = accountSid;
            return this;
        }

        public Builder applicationSid(String applicationSid) {
            this.applicationSid = applicationSid;
            return this;
        }

        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder country(String country) {
            this.country = country;
            return this;
        }

        public Builder languageCode(String languageCode) {
            this.languageCode = languageCode;
            return this;
        }

        public Builder token(List<TokenFcm> tokenFcms) {
            this.tokenFcms = tokenFcms;
            return this;
        }


        public Builder device(String device) {
            this.device = device;
            return this;
        }

        public Builder platform(String platform) {
            this.platform = platform;
            return this;
        }

        public User build() {

            User build = new User(this);

            build.checkEmail(build.getEmail());

            return build;
        }
    }

    private void checkEmail(String email) {
        Utils.notNull(email, "Email cannot be null");
        Utils.notEmpty(email, "Email cannot be empty");
    }
}
