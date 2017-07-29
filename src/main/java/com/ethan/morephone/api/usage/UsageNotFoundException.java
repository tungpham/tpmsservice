package com.ethan.morephone.api.usage;

/**
 * Created by truongnguyen on 7/15/17.
 */
public class UsageNotFoundException extends RuntimeException {

    public UsageNotFoundException(String id) {
        super(String.format("No Usage entry found with id: <%s>", id));
    }
}
