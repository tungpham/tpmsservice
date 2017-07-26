package com.ethan.morephone.api.purchase;

/**
 * Created by truongnguyen on 7/15/17.
 */
public class PurchaseNotFoundException extends RuntimeException {

    public PurchaseNotFoundException(String id) {
        super(String.format("No Purchase entry found with id: <%s>", id));
    }
}
