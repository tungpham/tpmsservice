package com.ethan.morephone.api.contact.controller;

import com.ethan.morephone.api.contact.domain.ContactDTO;
import com.ethan.morephone.api.contact.service.ContactService;
import com.ethan.morephone.http.HTTPStatus;
import com.ethan.morephone.http.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Created by truongnguyen on 9/28/17.
 */
@RestController
@RequestMapping("/api/v1/contact")
final class ContactController {

    private final ContactService mContactService;

    @Autowired
    ContactController(ContactService contactService) {
        this.mContactService = contactService;
    }

    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    Response<Object> delete(@PathVariable("id") String id) {

        ContactDTO contactDTO = mContactService.findById(id);
        if (contactDTO != null) {
            mContactService.delete(id);
            return new Response<>(HTTPStatus.OK.getReasonPhrase(), HTTPStatus.OK);
        } else {
            return new Response<>(HTTPStatus.NOT_FOUND.getReasonPhrase(), HTTPStatus.NOT_FOUND);
        }
    }


    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    Response<Object> create(@RequestBody @Valid ContactDTO contactEntry) {
        ContactDTO contactDTO = mContactService.findByPhoneNumber(contactEntry.getPhoneNumber());
        if (contactDTO != null) {
            return new Response<>(HTTPStatus.SEE_OTHER.getReasonPhrase(), HTTPStatus.SEE_OTHER);
        } else {
            ContactDTO dto = mContactService.create(contactEntry);
            return new Response<>(dto, HTTPStatus.CREATED);
        }
    }

    @RequestMapping(method = RequestMethod.PUT, produces = "application/json")
    Response<Object> update(@RequestBody @Valid ContactDTO contactEntry) {
        ContactDTO contactDTO = mContactService.findById(contactEntry.getId());
        if (contactDTO != null) {
            mContactService.update(contactEntry);
            return new Response<>(contactEntry, HTTPStatus.OK);
        } else {
            return new Response<>(HTTPStatus.NOT_FOUND.getReasonPhrase(), HTTPStatus.NOT_FOUND);
        }
    }

}
