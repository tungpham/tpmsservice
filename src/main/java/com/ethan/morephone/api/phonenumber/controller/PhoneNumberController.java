package com.ethan.morephone.api.phonenumber.controller;

import com.ethan.morephone.api.phonenumber.domain.PhoneNumberDTO;
import com.ethan.morephone.api.phonenumber.service.PhoneNumberService;
import com.ethan.morephone.api.user.UserNotFoundException;
import com.ethan.morephone.data.entity.phonenumbers.IncomingPhoneNumber;
import com.ethan.morephone.data.network.ApiManager;
import com.ethan.morephone.http.HTTPStatus;
import com.ethan.morephone.http.Response;
import com.ethan.morephone.utils.Utils;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.IncomingPhoneNumberDeleter;
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
        Utils.logMessage(todoEntry.getSid());
        PhoneNumberDTO phoneNumberDTO = service.findBySid(todoEntry.getSid());

        if (phoneNumberDTO == null) {
            PhoneNumberDTO created = service.create(todoEntry);
            Utils.logMessage("CREATE NEW PHONE NUMBER: " + created);
//            Register sms Application
            IncomingPhoneNumber incomingPhoneNumber = ApiManager.modifyIncomingPhoneNumber(
                    todoEntry.getAccountSid(),
                    todoEntry.getAuthToken(),
                    created.getSid(),
                    todoEntry.getApplicationSid(),
                    "POST",
                    todoEntry.getApplicationSid(),
                    "POST"
            );

            if (incomingPhoneNumber != null) {
                return new Response<>(created, HTTPStatus.CREATED);
            } else {
                return new Response<>(HTTPStatus.NOT_ACCEPTABLE.getReasonPhrase(), HTTPStatus.NOT_ACCEPTABLE);
            }
        } else {

            return new Response<>(HTTPStatus.SEE_OTHER.getReasonPhrase(), HTTPStatus.SEE_OTHER);
        }
    }

    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    Response<Object> delete(@PathVariable("id") String id,
                            @RequestParam("account_sid") String accountSid,
                            @RequestParam("auth_token") String authToken) {
        PhoneNumberDTO deleted = service.delete(id);
        Utils.logMessage("accountSid: " + accountSid);
        Utils.logMessage("authToken: " + authToken);
        if (deleted != null) {
            Twilio.init(accountSid, authToken);
            IncomingPhoneNumberDeleter deleter = new IncomingPhoneNumberDeleter(id);
            try {
                if (deleter.delete()) {
                    Utils.logMessage("DELETE PHONE NUMBER SUCCESS ");
                    return new Response<>(deleted, HTTPStatus.OK);
                } else {
                    Utils.logMessage("DELETE PHONE NUMBER ERROR ");
                    return new Response<>(HTTPStatus.NOT_FOUND.getReasonPhrase(), HTTPStatus.NOT_FOUND);
                }
            } catch (Exception e) {
                return new Response<>(HTTPStatus.NOT_FOUND.getReasonPhrase(), HTTPStatus.NOT_FOUND);
            }
        } else {
            return new Response<>(HTTPStatus.NOT_FOUND.getReasonPhrase(), HTTPStatus.NOT_FOUND);
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    List<PhoneNumberDTO> findAll() {

        List<PhoneNumberDTO> userDTOS = service.findAll();

        return userDTOS;
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    PhoneNumberDTO findById(@PathVariable("id") String id) {

        PhoneNumberDTO userDTO = service.findById(id);

        return userDTO;
    }

    @RequestMapping(value = "{id}", method = RequestMethod.PUT)
    PhoneNumberDTO update(@RequestBody @Valid PhoneNumberDTO todoEntry) {

        PhoneNumberDTO updated = service.update(todoEntry);

        return updated;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handleTodoNotFound(UserNotFoundException ex) {
    }

}
