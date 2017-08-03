package com.ethan.morephone.api.user.controller;

import com.ethan.morephone.Constants;
import com.ethan.morephone.api.usage.domain.UsageDTO;
import com.ethan.morephone.api.usage.service.UsageService;
import com.ethan.morephone.api.user.UserNotFoundException;
import com.ethan.morephone.api.user.domain.UserDTO;
import com.ethan.morephone.api.user.service.UserService;
import com.ethan.morephone.http.HTTPStatus;
import com.ethan.morephone.http.Response;
import com.ethan.morephone.utils.Utils;
import com.twilio.Twilio;
import com.twilio.http.HttpMethod;
import com.twilio.rest.api.v2010.account.ApplicationCreator;
import com.twilio.rest.notify.v1.service.Binding;
import com.twilio.rest.notify.v1.service.BindingCreator;
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
    private final UsageService mUsageService;

    @Autowired
    UserController(UserService service, UsageService usageService) {
        this.service = service;
        this.mUsageService = usageService;
    }

    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    Response<Object> create(@RequestBody @Valid UserDTO todoEntry) {
        LOGGER.info("Creating a new user entry with information: {}", todoEntry);

        UserDTO userDTO = service.findByEmail(todoEntry.getEmail());
        if (userDTO == null) {


            Utils.logMessage("ACCOUNTSID: " + todoEntry.getAccountSid());
            Utils.logMessage("AUTH CODE : " + todoEntry.getAuthToken());
            Twilio.init(todoEntry.getAccountSid(), todoEntry.getAuthToken());

            com.twilio.rest.api.v2010.account.Application applicationCreator = new ApplicationCreator(todoEntry.getEmail()).setVoiceUrl(Constants.VOICE_URL).setVoiceMethod(HttpMethod.POST)
                    .setSmsUrl(Constants.MESSAGE_URL).setSmsMethod(HttpMethod.POST).create();

            if (applicationCreator != null) {
                Utils.logMessage("CREATE APPLICATION SUCCESS");
                UserDTO created = service.create(todoEntry);
                created.setApplicationSid(applicationCreator.getSid());

                Utils.logMessage("Created a new user entry with information: {}" + created);

                UsageDTO usageDTO = new UsageDTO(created.getId(), 0, 0, 0, 0, 0);
                mUsageService.create(usageDTO);

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
        LOGGER.info("Deleting user entry with id: {}", id);

        UserDTO deleted = service.delete(id);
        LOGGER.info("Deleted user entry with information: {}", deleted);

        return deleted;
    }

    @RequestMapping(method = RequestMethod.GET)
    List<UserDTO> findAll() {
        LOGGER.info("Finding all user entries");

        List<UserDTO> userDTOS = service.findAll();
        LOGGER.info("Found {} user entries", userDTOS.size());

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
        LOGGER.info("Updating user entry with token:", token);

        UserDTO updated = service.updateToken(id, token);
        LOGGER.info("Updated user entry with information: {}", updated);

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


    public void bindingUser(String identity, String address) {
        Twilio.init(Constants.TWILIO_API_KEY, Constants.TWILIO_API_SECRET, Constants.TWILIO_ACCOUNT_SID);
        try {
            // Convert BindingType from Object to enum value
            Binding.BindingType bindingType = Binding.BindingType.forValue(FCM_BINDING_TYPE);
            // Add the notification service sid

            Utils.logMessage("SERVICE: " + Constants.TWILIO_NOTIFICATION_SERVICE_SID);
            Utils.logMessage("bindingType: " + bindingType);
            Utils.logMessage("identity: " + identity);
            Utils.logMessage("ADDRESS: " + address);
            // Create the binding
            BindingCreator bindingCreator = new BindingCreator(Constants.TWILIO_NOTIFICATION_SERVICE_SID, "", identity, bindingType, address);
            Binding binding = bindingCreator.create();

            // Send a JSON response indicating success
            Utils.logMessage("BINDING SUCCESS: " + identity);
        } catch (Exception ex) {
            // Send a JSON response indicating an error
            Utils.logMessage("BINDING ERROR: " + ex.getMessage());
        }
    }

}
