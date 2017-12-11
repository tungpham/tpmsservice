package com.ethan.morephone.api.country.controller;

import com.twilio.Twilio;
import com.twilio.base.ResourceSet;
import com.twilio.rest.api.v2010.account.AvailablePhoneNumberCountry;
import com.twilio.rest.api.v2010.account.AvailablePhoneNumberCountryReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by truongnguyen on 12/6/17.
 */
@RestController
@RequestMapping("/api/v1/phonenumber/country")
public class CountryPhoneNumber {


    @Autowired
    CountryPhoneNumber() {

    }


    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?> findCountry(@RequestParam(value = "account_sid") String accountSid,
                                         @RequestParam(value = "auth_token") String authToken) {

        Twilio.init(accountSid, authToken);
        AvailablePhoneNumberCountryReader reader = new AvailablePhoneNumberCountryReader(accountSid);
        ResourceSet<AvailablePhoneNumberCountry> availablePhoneNumberCountries = reader.read();
        if (availablePhoneNumberCountries != null && availablePhoneNumberCountries.iterator() != null) {
            return new ResponseEntity<>(availablePhoneNumberCountries.iterator(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST.getReasonPhrase(), HttpStatus.BAD_REQUEST);
        }
    }
}
