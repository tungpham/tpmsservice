package com.ethan.morephone.api.available;

/**
 * Created by truongnguyen on 7/15/17.
 */
public class AvailablePhoneNumberNotFoundException extends RuntimeException {

    public AvailablePhoneNumberNotFoundException(String id) {
        super(String.format("No account entry found with id: <%s>", id));
    }
}
