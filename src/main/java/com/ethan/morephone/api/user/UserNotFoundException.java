package com.ethan.morephone.api.user;

/**
 * Created by truongnguyen on 7/15/17.
 */
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String id) {
        super(String.format("No User entry found with id: <%s>", id));
    }
}
