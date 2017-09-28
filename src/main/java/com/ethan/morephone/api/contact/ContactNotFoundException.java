package com.ethan.morephone.api.contact;

/**
 * Created by truongnguyen on 7/15/17.
 */
public class ContactNotFoundException extends RuntimeException {

    public ContactNotFoundException(String id) {
        super(String.format("No Contact entry found with id: <%s>", id));
    }
}
