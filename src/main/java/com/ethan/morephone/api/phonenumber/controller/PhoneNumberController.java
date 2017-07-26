package com.ethan.morephone.api.phonenumber.controller;

import com.ethan.morephone.api.phonenumber.domain.PhoneNumberDTO;
import com.ethan.morephone.api.phonenumber.service.PhoneNumberService;
import com.ethan.morephone.api.user.UserNotFoundException;
import com.ethan.morephone.http.HTTPStatus;
import com.ethan.morephone.http.Response;
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
@RequestMapping("/api/phone-number")
final class PhoneNumberController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PhoneNumberController.class);

    private final PhoneNumberService service;

    @Autowired
    PhoneNumberController(PhoneNumberService service) {
        this.service = service;
    }

    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    Response<Object> create(@RequestBody @Valid PhoneNumberDTO todoEntry) {
        LOGGER.info("Creating a new user entry with information: {}", todoEntry);

        PhoneNumberDTO userDTO = service.findBySid(todoEntry.getSid());
        if (userDTO == null) {
            PhoneNumberDTO created = service.create(todoEntry);
            LOGGER.info("Created a new user entry with information: {}", created);
            return new Response<>(created, HTTPStatus.CREATED);
        } else {
            return new Response<>(HTTPStatus.SEE_OTHER.getReasonPhrase(), HTTPStatus.SEE_OTHER);
        }
    }

    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    PhoneNumberDTO delete(@PathVariable("id") String id) {
        LOGGER.info("Deleting user entry with id: {}", id);

        PhoneNumberDTO deleted = service.delete(id);
        LOGGER.info("Deleted user entry with information: {}", deleted);

        return deleted;
    }

    @RequestMapping(method = RequestMethod.GET)
    List<PhoneNumberDTO> findAll() {
        LOGGER.info("Finding all user entries");

        List<PhoneNumberDTO> userDTOS = service.findAll();
        LOGGER.info("Found {} user entries", userDTOS.size());

        return userDTOS;
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    PhoneNumberDTO findById(@PathVariable("id") String id) {
        LOGGER.info("Finding user entry with id: {}", id);

        PhoneNumberDTO userDTO = service.findById(id);
        LOGGER.info("Found user entry with information: {}", userDTO);

        return userDTO;
    }

    @RequestMapping(value = "{id}", method = RequestMethod.PUT)
    PhoneNumberDTO update(@RequestBody @Valid PhoneNumberDTO todoEntry) {
        LOGGER.info("Updating user entry with information: {}", todoEntry);

        PhoneNumberDTO updated = service.update(todoEntry);
        LOGGER.info("Updated user entry with information: {}", updated);

        return updated;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handleTodoNotFound(UserNotFoundException ex) {
        LOGGER.error("Handling error with message: {}", ex.getMessage());
    }

}
