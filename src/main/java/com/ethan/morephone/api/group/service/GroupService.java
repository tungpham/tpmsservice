package com.ethan.morephone.api.group.service;


import com.ethan.morephone.api.group.domain.GroupDTO;

import java.util.List;

/**
 * Created by truongnguyen on 7/15/17.
 */
public interface GroupService {

    /**
     * Creates a new user entry.
     * @param groupDTO  The information of the created user entry.
     * @return      The information of the created user entry.
     */
    GroupDTO create(GroupDTO groupDTO);

    /**
     * Deletes a user entry.
     * @param sid    The id of the deleted user entry.
     * @return      The information of the deleted user entry.
     * @throws      com.ethan.morephone.api.phonenumber.PhoneNumberNotFoundException if no user entry is found.
     */
    GroupDTO delete(String sid);

    /**
     * Finds all user entries.
     * @return      The information of all user entries.
     */
    List<GroupDTO> findAll();

    /**
     * Finds a single user entry.
     * @param id    The id of the requested user entry.
     * @return      The information of the requested user entry.
     * @throws      com.ethan.morephone.api.phonenumber.PhoneNumberNotFoundException if no user entry is found.
     */
    GroupDTO findById(String id);

    /**
     * Updates the information of a user entry.
     * @param user  The information of the updated user entry.
     * @return      The information of the updated user entry.
     * @throws      com.ethan.morephone.api.phonenumber.PhoneNumberNotFoundException if no user entry is found.
     */
    GroupDTO update(GroupDTO user);

    List<GroupDTO> findByPhoneNumberId(String phoneNumberId);

    List<GroupDTO> findByUserId(String userId);

    GroupDTO findByName(String name);

}
