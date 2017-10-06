package com.ethan.morephone.api.messagegroup.controller;

import com.ethan.morephone.api.messagegroup.domain.MessageGroupDTO;
import com.ethan.morephone.api.messagegroup.service.MessageGroupService;
import com.ethan.morephone.http.HTTPStatus;
import com.ethan.morephone.http.Response;
import com.ethan.morephone.utils.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Created by truongnguyen on 9/28/17.
 */
@RestController
@RequestMapping("/api/v1/message-group")
final class MessageGroupController {

    private final MessageGroupService mMessageGroupService;

    @Autowired
    MessageGroupController(MessageGroupService contactService) {
        this.mMessageGroupService = contactService;
    }

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    Response<Object> loadMessageGroup(@RequestParam("phone_number_id") String phoneNumberId) {
        if (TextUtils.isEmpty(phoneNumberId)) {
            return new Response<>(HTTPStatus.BAD_REQUEST.getReasonPhrase(), HTTPStatus.BAD_REQUEST);
        }

        List<MessageGroupDTO> messageGroupDTOS = mMessageGroupService.findByPhoneNumberId(phoneNumberId);
        if (messageGroupDTOS != null && !messageGroupDTOS.isEmpty()) {
            return new Response<>(messageGroupDTOS, HTTPStatus.OK);
        } else {
            return new Response<>(HTTPStatus.NOT_FOUND, HTTPStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/user", method = RequestMethod.GET, produces = "application/json")
    Response<Object> loadMessageGroupByUser(@RequestParam("user_id") String userId) {
        if (TextUtils.isEmpty(userId)) {
            return new Response<>(HTTPStatus.BAD_REQUEST.getReasonPhrase(), HTTPStatus.BAD_REQUEST);
        }

        List<MessageGroupDTO> messageGroupDTOS = mMessageGroupService.findByUserId(userId);
        if (messageGroupDTOS != null && !messageGroupDTOS.isEmpty()) {
            return new Response<>(messageGroupDTOS, HTTPStatus.OK);
        } else {
            return new Response<>(HTTPStatus.NOT_FOUND, HTTPStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    Response<Object> delete(@PathVariable("id") String id) {

        MessageGroupDTO contactDTO = mMessageGroupService.findById(id);
        if (contactDTO != null) {
            mMessageGroupService.delete(id);
            return new Response<>(HTTPStatus.OK.getReasonPhrase(), HTTPStatus.OK);
        } else {
            return new Response<>(HTTPStatus.NOT_FOUND.getReasonPhrase(), HTTPStatus.NOT_FOUND);
        }
    }


    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    Response<Object> create(@RequestBody @Valid MessageGroupDTO contactEntry) {
        MessageGroupDTO dto = mMessageGroupService.create(contactEntry);
        return new Response<>(dto, HTTPStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.PUT, produces = "application/json")
    Response<Object> update(@RequestBody @Valid MessageGroupDTO contactEntry) {
        MessageGroupDTO messageGroupDTO = mMessageGroupService.findById(contactEntry.getId());
        if (messageGroupDTO != null) {
            mMessageGroupService.update(contactEntry);
            return new Response<>(contactEntry, HTTPStatus.OK);
        } else {
            return new Response<>(HTTPStatus.NOT_FOUND.getReasonPhrase(), HTTPStatus.NOT_FOUND);
        }
    }

}
