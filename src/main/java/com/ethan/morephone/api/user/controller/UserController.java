package com.ethan.morephone.api.user.controller;

import com.ethan.morephone.Constants;
import com.ethan.morephone.api.phonenumber.domain.PhoneNumberDTO;
import com.ethan.morephone.api.phonenumber.service.PhoneNumberService;
import com.ethan.morephone.api.usage.domain.UsageDTO;
import com.ethan.morephone.api.usage.service.UsageService;
import com.ethan.morephone.api.user.UserNotFoundException;
import com.ethan.morephone.api.user.domain.UserDTO;
import com.ethan.morephone.api.user.service.UserService;
import com.ethan.morephone.http.HTTPStatus;
import com.ethan.morephone.http.Response;
import com.ethan.morephone.utils.Utils;
import com.twilio.Twilio;
import com.twilio.base.ResourceSet;
import com.twilio.http.HttpMethod;
import com.twilio.rest.api.v2010.account.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Created by truongnguyen on 7/14/17.
 */
@RestController
@RequestMapping("/api/v1/user")
final class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    private static final String FCM_BINDING_TYPE = "fcm";

    private final UserService service;
    private final PhoneNumberService mPhoneNumberService;
    private final UsageService mUsageService;

    @Autowired
    UserController(UserService service, PhoneNumberService phoneNumberService, UsageService usageService) {
        this.service = service;
        this.mPhoneNumberService = phoneNumberService;
        this.mUsageService = usageService;
    }

    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    Response<Object> create(@RequestBody @Valid UserDTO todoEntry) {

        //Check user exist
        UserDTO userDTO = service.findByEmail(todoEntry.getEmail());

        if (userDTO == null) {

            Utils.logMessage("ACCOUNT SID: " + todoEntry.getAccountSid());

            Twilio.init(todoEntry.getAccountSid(), todoEntry.getAuthToken());

            /*-------------------------------Check application twilio if exits will modifer or create new--------------------------------*/
            String applicationName = todoEntry.getEmail();
            Application application;

            ResourceSet<Application> applications = new ApplicationReader("AC1bb60516853a77bcf93ea89e4a7e3b45").read();

            Utils.logMessage("APPLICATIONS");

            if (applications != null && applications.iterator().hasNext()) {
                application = applications.iterator().next();

                application = new ApplicationUpdater(application.getSid())
                        .setFriendlyName(applicationName)
                        .setVoiceUrl(Constants.VOICE_URL)
                        .setVoiceMethod(HttpMethod.POST)
                        .setSmsUrl(Constants.MESSAGE_URL)
                        .setSmsMethod(HttpMethod.POST)
                        .update();

                Utils.logMessage("APPLICATIONS UPDATE");

            } else {

                application = new ApplicationCreator(
                        applicationName)
                        .setVoiceUrl(Constants.VOICE_URL)
                        .setVoiceMethod(HttpMethod.POST)
                        .setSmsUrl(Constants.MESSAGE_URL)
                        .setSmsMethod(HttpMethod.POST)
                        .create();

                Utils.logMessage("APPLICATIONS CREATE");

            }

            /*----------------------END APPLICATION-------------------------*/

            if (application != null) {

                 /*----------------------CREATE USER -----------------------*/
                UserDTO created = service.create(todoEntry);
                created.setApplicationSid(application.getSid());

                /*----------------------CREATE USAGE -----------------------*/
                UsageDTO usageDTO = new UsageDTO(created.getId(), created.getAccountSid(), 0, 0, 0, 0, 0);
                mUsageService.create(usageDTO);

                /*----------------------SYNC PHONE NUMBER -----------------------*/
                ResourceSet<IncomingPhoneNumber> incomingPhoneNumbers = new IncomingPhoneNumberReader(todoEntry.getAccountSid()).read();
                if (incomingPhoneNumbers != null && incomingPhoneNumbers.iterator().hasNext()) {
                    for (IncomingPhoneNumber incomingPhoneNumber : incomingPhoneNumbers) {
                        PhoneNumberDTO phoneNumberDTO = mPhoneNumberService.findBySid(incomingPhoneNumber.getSid());
                        if (phoneNumberDTO == null) {
                            phoneNumberDTO = new PhoneNumberDTO();
                            phoneNumberDTO.setFriendlyName(incomingPhoneNumber.getFriendlyName());
                            phoneNumberDTO.setPhoneNumber(incomingPhoneNumber.getPhoneNumber().toString());
                            phoneNumberDTO.setSid(incomingPhoneNumber.getSid());
                            phoneNumberDTO.setUserId(created.getId());

                            mPhoneNumberService.create(phoneNumberDTO);
                        }
                    }
                }

                return new Response<>(created, HTTPStatus.CREATED);
            } else {
                return new Response<>(HTTPStatus.NOT_ACCEPTABLE.getReasonPhrase(), HTTPStatus.NOT_ACCEPTABLE);
            }

        } else {
            return new Response<>(userDTO, HTTPStatus.SEE_OTHER);
        }
    }

    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    UserDTO delete(@PathVariable("id") String id) {
        UserDTO deleted = service.delete(id);
        return deleted;
    }

    @RequestMapping(method = RequestMethod.GET)
    List<UserDTO> findAll() {
        List<UserDTO> userDTOS = service.findAll();
        return userDTOS;
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    UserDTO findById(@PathVariable("id") String id) {
        LOGGER.info("Finding user entry with id: {}", id);

        UserDTO userDTO = service.findById(id);
        LOGGER.info("Found user entry with information: {}", userDTO);

        return userDTO;
    }

    @RequestMapping(value = "{id}", method = RequestMethod.PUT)
    UserDTO update(@RequestBody @Valid UserDTO todoEntry) {
        LOGGER.info("Updating user entry with information: {}", todoEntry);

        UserDTO updated = service.update(todoEntry);
        LOGGER.info("Updated user entry with information: {}", updated);

        return updated;
    }

    @RequestMapping(value = "/{id}/token", method = RequestMethod.PUT)
    Response<Object> updateToken(@PathVariable("id") String id,
                                 @RequestParam(value = "token") String token) {

        UserDTO updated = service.updateToken(id, token);

        if (updated == null) {
            return new Response<>(HTTPStatus.NOT_FOUND.getReasonPhrase(), HTTPStatus.NOT_FOUND);
        } else {
            return new Response<>(updated, HTTPStatus.OK);
        }
    }



    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handleTodoNotFound(UserNotFoundException ex) {
        LOGGER.error("Handling error with message: {}", ex.getMessage());
    }

}
