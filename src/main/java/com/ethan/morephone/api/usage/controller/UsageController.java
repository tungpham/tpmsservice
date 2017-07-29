package com.ethan.morephone.api.usage.controller;

import com.ethan.morephone.api.usage.domain.UsageDTO;
import com.ethan.morephone.api.usage.service.UsageService;
import com.ethan.morephone.api.user.UserNotFoundException;
import com.ethan.morephone.http.HTTPStatus;
import com.ethan.morephone.http.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * Created by truongnguyen on 7/14/17.
 */
@RestController
@RequestMapping("/api/v1/usage")
final class UsageController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UsageController.class);

    private final UsageService service;

    @Autowired
    UsageController(UsageService service) {
        this.service = service;
    }


    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    Response<Object> findByUserId(@PathVariable("id") String userId) {
        LOGGER.info("Finding usage entry with id: {}", userId);

        UsageDTO usageDTO = service.findByUserId(userId);
        LOGGER.info("Found usage entry with information: {}", usageDTO);

        if (usageDTO == null) {
            return new Response<>(HTTPStatus.NOT_FOUND.getReasonPhrase(), HTTPStatus.NOT_FOUND);
        }else{
            return new Response<>(usageDTO, HTTPStatus.OK);
        }
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handleTodoNotFound(UserNotFoundException ex) {
        LOGGER.error("Handling error with message: {}", ex.getMessage());
    }

}
