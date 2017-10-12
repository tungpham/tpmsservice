package com.ethan.morephone.api.messagegroup;

/**
 * Created by truongnguyen on 7/15/17.
 */
public class MessageGroupNotFoundException extends RuntimeException {

    public MessageGroupNotFoundException(String id) {
        super(String.format("No Group entry found with id: <%s>", id));
    }
}
