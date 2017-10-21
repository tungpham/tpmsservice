package com.ethan.morephone.api.messagegroup.service;


import com.ethan.morephone.api.messagegroup.domain.MessageGroupDTO;

import java.util.HashMap;
import java.util.List;

/**
 * Created by truongnguyen on 7/15/17.
 */
public interface MessageGroupService {

    /**
     * Creates a new user entry.
     * @param messageGroupDTO  The information of the created user entry.
     * @return      The information of the created user entry.
     */
    MessageGroupDTO create(MessageGroupDTO messageGroupDTO);

    /**
     * Deletes a user entry.
     * @param sid    The id of the deleted user entry.
     * @return      The information of the deleted user entry.
     * @throws      com.ethan.morephone.api.phonenumber.PhoneNumberNotFoundException if no user entry is found.
     */
    MessageGroupDTO delete(String sid);

    /**
     * Finds all user entries.
     * @return      The information of all user entries.
     */
    List<MessageGroupDTO> findAll();

    /**
     * Finds a single user entry.
     * @param id    The id of the requested user entry.
     * @return      The information of the requested user entry.
     * @throws      com.ethan.morephone.api.phonenumber.PhoneNumberNotFoundException if no user entry is found.
     */
    MessageGroupDTO findById(String id);

    /**
     * Updates the information of a user entry.
     * @param user  The information of the updated user entry.
     * @return      The information of the updated user entry.
     * @throws      com.ethan.morephone.api.phonenumber.PhoneNumberNotFoundException if no user entry is found.
     */
    MessageGroupDTO update(MessageGroupDTO user);

    List<MessageGroupDTO> findByPhoneNumberId(String phoneNumberId);

    List<MessageGroupDTO> findByUserId(String userId);

    HashMap<String, MessageGroupDTO> getMessageGroupHashMap();

}
