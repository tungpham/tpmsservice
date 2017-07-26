package com.ethan.morephone.api.phonenumber.service;


import com.ethan.morephone.api.phonenumber.domain.PhoneNumberDTO;
import com.ethan.morephone.api.user.domain.UserDTO;

import java.util.List;

/**
 * Created by truongnguyen on 7/15/17.
 */
public interface PhoneNumberService {

    /**
     * Creates a new user entry.
     * @param user  The information of the created user entry.
     * @return      The information of the created user entry.
     */
    PhoneNumberDTO create(PhoneNumberDTO user);

    /**
     * Deletes a user entry.
     * @param id    The id of the deleted user entry.
     * @return      The information of the deleted user entry.
     * @throws      com.ethan.morephone.UserNotFoundException if no user entry is found.
     */
    PhoneNumberDTO delete(String id);

    /**
     * Finds all user entries.
     * @return      The information of all user entries.
     */
    List<PhoneNumberDTO> findAll();

    /**
     * Finds a single user entry.
     * @param id    The id of the requested user entry.
     * @return      The information of the requested user entry.
     * @throws      com.ethan.tracking.UserNotFoundException if no user entry is found.
     */
    PhoneNumberDTO findById(String id);

    /**
     * Updates the information of a user entry.
     * @param user  The information of the updated user entry.
     * @return      The information of the updated user entry.
     * @throws      com.ethan.tracking.UserNotFoundException if no user entry is found.
     */
    PhoneNumberDTO update(PhoneNumberDTO user);

    PhoneNumberDTO findBySid(String sid);

    PhoneNumberDTO findByPhoneNumber(String phoneNumber);
}
