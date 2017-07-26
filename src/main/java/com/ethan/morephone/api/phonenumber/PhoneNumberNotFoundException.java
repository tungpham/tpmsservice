package com.ethan.morephone.api.phonenumber;

/**
 * Created by truongnguyen on 7/15/17.
 */
public class PhoneNumberNotFoundException extends RuntimeException {

    public PhoneNumberNotFoundException(String id) {
        super(String.format("No Phone Number entry found with id: <%s>", id));
    }
}
