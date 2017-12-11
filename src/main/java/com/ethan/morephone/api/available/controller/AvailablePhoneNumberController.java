package com.ethan.morephone.api.available.controller;

import com.twilio.Twilio;
import com.twilio.base.ResourceSet;
import com.twilio.rest.api.v2010.account.availablephonenumbercountry.*;
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
@RequestMapping("/api/v1/phonenumber/available")
public class AvailablePhoneNumberController {

    @Autowired
    AvailablePhoneNumberController() {
    }


    @RequestMapping(value = "/local", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?> findLocal(@RequestParam(value = "account_sid") String accountSid,
                                       @RequestParam(value = "auth_token") String authToken,
                                       @RequestParam(value = "country_code") String countryCode,
                                       @RequestParam(value = "sms_enabled") boolean smsEnabled,
                                       @RequestParam(value = "mms_enabled") boolean mmsEnabled,
                                       @RequestParam(value = "voice_enabled") boolean voiceEnabled,
                                       @RequestParam(value = "contain") String contain) {

        Twilio.init(accountSid, authToken);
        LocalReader localReader = new LocalReader(accountSid, countryCode)
                .setSmsEnabled(smsEnabled)
                .setMmsEnabled(mmsEnabled)
                .setContains(contain)
                .setVoiceEnabled(voiceEnabled);

        ResourceSet<Local> locals = localReader.read();

        if (locals != null && locals.iterator().hasNext()) {
            return new ResponseEntity<>(locals.iterator(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST.getReasonPhrase(), HttpStatus.BAD_REQUEST);
        }

    }

    @RequestMapping(value = "/mobile", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?> findMobile(@RequestParam(value = "account_sid") String accountSid,
                                        @RequestParam(value = "auth_token") String authToken,
                                        @RequestParam(value = "country_code") String countryCode,
                                        @RequestParam(value = "sms_enabled") boolean smsEnabled,
                                        @RequestParam(value = "mms_enabled") boolean mmsEnabled,
                                        @RequestParam(value = "voice_enabled") boolean voiceEnabled,
                                        @RequestParam(value = "contain") String contain) {

        Twilio.init(accountSid, authToken);
        MobileReader mobileReader = new MobileReader(accountSid, countryCode)
                .setSmsEnabled(smsEnabled)
                .setMmsEnabled(mmsEnabled)
                .setVoiceEnabled(voiceEnabled)
                .setContains(contain);

        ResourceSet<Mobile> mobiles = mobileReader.read();
        if (mobiles != null && mobiles.iterator() != null) {
            return new ResponseEntity<>(mobiles.iterator(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST.getReasonPhrase(), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/tollfree", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?> findTollFree(@RequestParam(value = "account_sid") String accountSid,
                                          @RequestParam(value = "auth_token") String authToken,
                                          @RequestParam(value = "country_code") String countryCode,
                                          @RequestParam(value = "sms_enabled") boolean smsEnabled,
                                          @RequestParam(value = "mms_enabled") boolean mmsEnabled,
                                          @RequestParam(value = "voice_enabled") boolean voiceEnabled,
                                          @RequestParam(value = "contain") String contain) {

        Twilio.init(accountSid, authToken);
        TollFreeReader tollFreeReader = new TollFreeReader(accountSid, countryCode)
                .setSmsEnabled(smsEnabled)
                .setMmsEnabled(mmsEnabled)
                .setVoiceEnabled(voiceEnabled)
                .setContains(contain);

        ResourceSet<TollFree> tollFrees = tollFreeReader.read();
        if (tollFrees != null && tollFrees.iterator() != null) {
            return new ResponseEntity<>(tollFrees.iterator(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST.getReasonPhrase(), HttpStatus.BAD_REQUEST);
        }
    }
}
