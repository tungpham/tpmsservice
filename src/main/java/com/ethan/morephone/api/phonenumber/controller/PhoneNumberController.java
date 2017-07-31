package com.ethan.morephone.api.phonenumber.controller;

import com.ethan.morephone.api.phonenumber.domain.PhoneNumberDTO;
import com.ethan.morephone.api.phonenumber.service.PhoneNumberService;
import com.ethan.morephone.api.user.UserNotFoundException;
import com.ethan.morephone.data.entity.phonenumbers.IncomingPhoneNumber;
import com.ethan.morephone.data.network.ApiManager;
import com.ethan.morephone.http.HTTPStatus;
import com.ethan.morephone.http.Response;
import com.ethan.morephone.utils.Utils;
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
    PhoneNumberDTO delete(@PathVariable("id") String id) {

        PhoneNumberDTO deleted = service.delete(id);

        return deleted;
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
