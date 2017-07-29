package com.ethan.morephone.api.purchase.controller;

import com.ethan.morephone.api.purchase.domain.PurchaseDTO;
import com.ethan.morephone.api.purchase.service.PurchaseService;
import com.ethan.morephone.api.user.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Created by truongnguyen on 7/14/17.
 */
@RestController
@RequestMapping("/api/v1/purchase")
final class PurchaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PurchaseController.class);

    private final PurchaseService service;

    @Autowired
    PurchaseController(PurchaseService service) {
        this.service = service;
    }

    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    PurchaseDTO create(@RequestBody @Valid PurchaseDTO todoEntry) {
        LOGGER.info("Creating a new purchase entry with information: {}", todoEntry);

        PurchaseDTO created = service.create(todoEntry);
        LOGGER.info("Created a new purchase entry with information: {}", created);

        return created;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handleTodoNotFound(UserNotFoundException ex) {
        LOGGER.error("Handling error with message: {}", ex.getMessage());
    }

}
