package com.ethan.morephone.api.purchase.domain;

import com.ethan.morephone.utils.Utils;
import org.springframework.data.annotation.Id;

/**
 * Created by truongnguyen on 7/20/17.
 */
public class Purchase {

    @Id
    private String id;
    private String userId;
    private String packageName;
    private String token;
    private int purchaseState;
    private String orderId;
    private long purchaseTime;
    private String productId;
    private long createdAt;
    private long updatedAt;


    public Purchase() {
    }

    private Purchase(Purchase.Builder builder) {
        this.userId = builder.userId;
        this.packageName = builder.packageName;
        this.token = builder.token;
        this.purchaseState = builder.purchaseState;
        this.orderId = builder.orderId;
        this.purchaseTime = builder.purchaseTime;
        this.productId = builder.productId;

    }

    public static Purchase.Builder getBuilder() {
        return new Purchase.Builder();
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

    public void setUserId(String email) {
        this.userId = email;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getPurchaseState() {
        return purchaseState;
    }

    public void setPurchaseState(int purchaseState) {
        this.purchaseState = purchaseState;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public long getPurchaseTime() {
        return purchaseTime;
    }

    public void setPurchaseTime(long purchaseTime) {
        this.purchaseTime = purchaseTime;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
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

    @Override
    public String toString() {
        return String.format(
                "User[id=%s, userId=%s, orderId=%s]",
                this.id,
                this.userId,
                this.orderId
        );
    }


    public static class Builder {
        private String userId;
        private String packageName;
        private String token;
        private int purchaseState;
        private String orderId;
        private long purchaseTime;
        private String productId;

        private Builder() {
        }


        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder packageName(String packageName) {
            this.packageName = packageName;
            return this;
        }

        public Builder token(String token) {
            this.packageName = token;
            return this;
        }

        public Builder purchaseState(int purchaseState) {
            this.purchaseState = purchaseState;
            return this;
        }

        public Builder orderId(String orderId) {
            this.orderId = orderId;
            return this;
        }

        public Builder purchaseTime(long purchaseTime) {
            this.purchaseTime = purchaseTime;
            return this;
        }

        public Builder productId(String productId) {
            this.productId = productId;
            return this;
        }


        public Purchase build() {

            Purchase build = new Purchase(this);

            build.checkUserId(build.getUserId());

            return build;
        }
    }

    private void checkUserId(String email) {
        Utils.notNull(email, "Email cannot be null");
        Utils.notEmpty(email, "Email cannot be empty");
    }
}
