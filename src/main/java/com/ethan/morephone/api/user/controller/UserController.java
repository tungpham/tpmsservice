package com.ethan.morephone.api.user.controller;

import com.ethan.morephone.api.user.UserNotFoundException;
import com.ethan.morephone.api.user.domain.UserDTO;
import com.ethan.morephone.api.user.service.UserService;
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
@RequestMapping("/api/v1/user")
final class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    private final UserService service;

    @Autowired
    UserController(UserService service) {
        this.service = service;
    }

    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    Response<Object> create(@RequestBody @Valid UserDTO todoEntry) {
        LOGGER.info("Creating a new user entry with information: {}", todoEntry);

        UserDTO userDTO = service.findByEmail(todoEntry.getEmail());
        if (userDTO == null) {
            UserDTO created = service.create(todoEntry);
            LOGGER.info("Created a new user entry with information: {}", created);

            return new Response<>(created, HTTPStatus.CREATED);
        } else {
            return new Response<>(HTTPStatus.SEE_OTHER.getReasonPhrase(), HTTPStatus.SEE_OTHER);
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
        }else{
            return new Response<>(updated, HTTPStatus.OK);
        }
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handleTodoNotFound(UserNotFoundException ex) {
        LOGGER.error("Handling error with message: {}", ex.getMessage());
    }

}
