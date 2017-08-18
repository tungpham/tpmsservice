package com.ethan.morephone.api.phonenumber.controller;

import com.ethan.morephone.api.phonenumber.domain.PhoneNumberDTO;
import com.ethan.morephone.api.phonenumber.service.PhoneNumberService;
import com.ethan.morephone.api.user.UserNotFoundException;
import com.ethan.morephone.http.HTTPStatus;
import com.ethan.morephone.http.Response;
import com.ethan.morephone.utils.TextUtils;
import com.ethan.morephone.utils.Utils;
import com.twilio.Twilio;
import com.twilio.http.HttpMethod;
import com.twilio.rest.api.v2010.account.IncomingPhoneNumberCreator;
import com.twilio.rest.api.v2010.account.IncomingPhoneNumberDeleter;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Created by truongnguyen on 7/14/17.
 */
@RestController
@RequestMapping("/api/v1/phone-number")
final class PhoneNumberController {


    private final PhoneNumberService service;

    @Autowired
    PhoneNumberController(PhoneNumberService service) {
        this.service = service;
    }

    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    Response<Object> create(@RequestBody @Valid PhoneNumberDTO todoEntry) {

        Utils.logMessage("APP SID: " + todoEntry.getApplicationSid());

        if (TextUtils.isEmpty(todoEntry.getApplicationSid())
                || TextUtils.isEmpty(todoEntry.getPhoneNumber())
                || TextUtils.isEmpty(todoEntry.getAccountSid())
                || TextUtils.isEmpty(todoEntry.getAuthToken())) {
            return new Response<>(HTTPStatus.BAD_REQUEST.getReasonPhrase(), HTTPStatus.BAD_REQUEST);
        }

        try {
            Twilio.init(todoEntry.getAccountSid(), todoEntry.getAuthToken());

            com.twilio.rest.api.v2010.account.IncomingPhoneNumber incomingPhoneNumber =
                    new IncomingPhoneNumberCreator(new PhoneNumber(todoEntry.getPhoneNumber()))
                            .setVoiceApplicationSid(todoEntry.getApplicationSid())
                            .setVoiceMethod(HttpMethod.POST)
                            .setSmsApplicationSid(todoEntry.getApplicationSid())
                            .setSmsMethod(HttpMethod.POST)
                            .create();

            Utils.logMessage("CREATE: " + todoEntry.getPhoneNumber());

            if (incomingPhoneNumber != null) {

                todoEntry.setSid(incomingPhoneNumber.getSid());
                todoEntry.setFriendlyName(incomingPhoneNumber.getFriendlyName());
                PhoneNumberDTO phoneNumberDTO = service.findBySid(todoEntry.getSid());
                if (phoneNumberDTO == null) {
                    PhoneNumberDTO created = service.create(todoEntry);
                    Utils.logMessage("CREATE NEW PHONE NUMBER: " + created);
                    return new Response<>(created, HTTPStatus.CREATED);
                } else {
                    return new Response<>(HTTPStatus.SEE_OTHER.getReasonPhrase(), HTTPStatus.SEE_OTHER);
                }

            } else {
                Utils.logMessage("CANNOT CREATE");
                return new Response<>(HTTPStatus.NOT_ACCEPTABLE.getReasonPhrase(), HTTPStatus.NOT_ACCEPTABLE);
            }

        } catch (Exception e) {
            Utils.logMessage("EROR CREATE");
            return new Response<>(HTTPStatus.NOT_ACCEPTABLE.getReasonPhrase(), HTTPStatus.NOT_ACCEPTABLE);
        }

    }

    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    Response<Object> delete(@PathVariable("id") String id,
                            @RequestParam("account_sid") String accountSid,
                            @RequestParam("auth_token") String authToken) {
//        PhoneNumberDTO deleted = service.delete(id);

        Utils.logMessage("accountSid: " + accountSid);
        Utils.logMessage("authToken: " + authToken);

//        if (deleted != null) {
        Twilio.init(accountSid, authToken);
        IncomingPhoneNumberDeleter deleter = new IncomingPhoneNumberDeleter(id);
        try {
            if (deleter.delete()) {
                Utils.logMessage("DELETE PHONE NUMBER SUCCESS ");
                return new Response<>(HTTPStatus.OK.getReasonPhrase(), HTTPStatus.OK);
            } else {
                Utils.logMessage("DELETE PHONE NUMBER ERROR ");
                return new Response<>(HTTPStatus.NOT_FOUND.getReasonPhrase(), HTTPStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new Response<>(HTTPStatus.NOT_FOUND.getReasonPhrase(), HTTPStatus.NOT_FOUND);
        }
//        } else {
//            return new Response<>(HTTPStatus.NOT_FOUND.getReasonPhrase(), HTTPStatus.NOT_FOUND);
//        }
    }

    @RequestMapping(value = "/{id}/forward", method = RequestMethod.PUT)
    Response<Object> updateForwardPhoneNumber(@PathVariable("id") String id,
                                              @RequestParam(value = "forward_phone_number") String forwardPhoneNumber,
                                              @RequestParam(value = "forward_email") String forwardEmail) {

        PhoneNumberDTO updated = service.updateForward(id, forwardPhoneNumber, forwardEmail);

        if (updated == null) {
            return new Response<>(HTTPStatus.NOT_FOUND.getReasonPhrase(), HTTPStatus.NOT_FOUND);
        } else {
            return new Response<>(updated, HTTPStatus.OK);
        }
    }

    @RequestMapping(value = "/{id}/forward/enable", method = RequestMethod.PUT)
    Response<Object> enableForwardPhoneNumber(@PathVariable("id") String id,
                                              @RequestParam(value = "is_forward") boolean isForward) {

        PhoneNumberDTO updated = service.enableForward(id, isForward);

        if (updated == null) {
            return new Response<>(HTTPStatus.NOT_FOUND.getReasonPhrase(), HTTPStatus.NOT_FOUND);
        } else {
            return new Response<>(updated, HTTPStatus.OK);
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    Response<Object> findPhoneNumberByUserId(@RequestParam("userId") String userId) {

        List<PhoneNumberDTO> phoneNumberDTOS = service.findByUserId(userId);

        if (phoneNumberDTOS == null) {
            return new Response<>(HTTPStatus.NOT_FOUND.getReasonPhrase(), HTTPStatus.NOT_FOUND);
        } else {
            return new Response<>(phoneNumberDTOS, HTTPStatus.OK);
        }
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    Response<Object> findById(@PathVariable("id") String id) {

        PhoneNumberDTO phoneNumberDTO = service.findById(id);

        if (phoneNumberDTO == null) {
            return new Response<>(HTTPStatus.NOT_FOUND.getReasonPhrase(), HTTPStatus.NOT_FOUND);
        } else {
            return new Response<>(phoneNumberDTO, HTTPStatus.OK);
        }
    }

    @RequestMapping(value = "{id}", method = RequestMethod.PUT)
    Response<Object> update(@RequestBody @Valid PhoneNumberDTO todoEntry) {

        PhoneNumberDTO updated = service.update(todoEntry);

        if (updated == null) {
            return new Response<>(HTTPStatus.NOT_FOUND.getReasonPhrase(), HTTPStatus.NOT_FOUND);
        } else {
            return new Response<>(updated, HTTPStatus.OK);
        }
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handleTodoNotFound(UserNotFoundException ex) {
    }

}
