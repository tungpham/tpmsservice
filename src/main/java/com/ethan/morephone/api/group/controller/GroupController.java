package com.ethan.morephone.api.group.controller;

import com.ethan.morephone.api.group.domain.GroupDTO;
import com.ethan.morephone.api.group.service.GroupService;
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
@RequestMapping("/api/v1/group")
final class GroupController {

    private final GroupService mGroupService;

    @Autowired
    GroupController(GroupService contactService) {
        this.mGroupService = contactService;
    }

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    Response<Object> loadMessageGroup(@RequestParam("phone_number_id") String phoneNumberId) {
        if (TextUtils.isEmpty(phoneNumberId)) {
            return new Response<>(HTTPStatus.BAD_REQUEST.getReasonPhrase(), HTTPStatus.BAD_REQUEST);
        }

        List<GroupDTO> groupDTOS = mGroupService.findByPhoneNumberId(phoneNumberId);
        if (groupDTOS != null && !groupDTOS.isEmpty()) {
            return new Response<>(groupDTOS, HTTPStatus.OK);
        } else {
            return new Response<>(HTTPStatus.NOT_FOUND, HTTPStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/user", method = RequestMethod.GET, produces = "application/json")
    Response<Object> loadMessageGroupByUser(@RequestParam("user_id") String userId) {
        if (TextUtils.isEmpty(userId)) {
            return new Response<>(HTTPStatus.BAD_REQUEST.getReasonPhrase(), HTTPStatus.BAD_REQUEST);
        }

        List<GroupDTO> groupDTOS = mGroupService.findByUserId(userId);
        if (groupDTOS != null && !groupDTOS.isEmpty()) {
            return new Response<>(groupDTOS, HTTPStatus.OK);
        } else {
            return new Response<>(HTTPStatus.NOT_FOUND, HTTPStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    Response<Object> delete(@PathVariable("id") String id) {

        GroupDTO contactDTO = mGroupService.findById(id);
        if (contactDTO != null) {
            mGroupService.delete(id);
            return new Response<>(HTTPStatus.OK.getReasonPhrase(), HTTPStatus.OK);
        } else {
            return new Response<>(HTTPStatus.NOT_FOUND.getReasonPhrase(), HTTPStatus.NOT_FOUND);
        }
    }


    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    Response<Object> create(@RequestBody @Valid GroupDTO contactEntry) {
        GroupDTO dto = mGroupService.create(contactEntry);
        return new Response<>(dto, HTTPStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.PUT, produces = "application/json")
    Response<Object> update(@RequestBody @Valid GroupDTO contactEntry) {
        GroupDTO groupDTO = mGroupService.findById(contactEntry.getId());
        if (groupDTO != null) {
            mGroupService.update(contactEntry);
            return new Response<>(contactEntry, HTTPStatus.OK);
        } else {
            return new Response<>(HTTPStatus.NOT_FOUND.getReasonPhrase(), HTTPStatus.NOT_FOUND);
        }
    }

}
