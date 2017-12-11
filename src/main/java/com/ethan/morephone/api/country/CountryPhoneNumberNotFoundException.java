package com.ethan.morephone.api.country;

/**
 * Created by truongnguyen on 7/15/17.
 */
public class CountryPhoneNumberNotFoundException extends RuntimeException {

    public CountryPhoneNumberNotFoundException(String id) {
        super(String.format("No account entry found with id: <%s>", id));
    }
}
