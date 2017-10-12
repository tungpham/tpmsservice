package com.ethan.morephone.api.group;

/**
 * Created by truongnguyen on 7/15/17.
 */
public class GroupNotFoundException extends RuntimeException {

    public GroupNotFoundException(String id) {
        super(String.format("No Message Group entry found with id: <%s>", id));
    }
}
