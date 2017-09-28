package com.ethan.morephone.api.contact.service;


import com.ethan.morephone.api.contact.domain.ContactDTO;

import java.util.List;

/**
 * Created by truongnguyen on 7/15/17.
 */
public interface ContactService {

    /**
     * Creates a new user entry.
     * @param contact  The information of the created user entry.
     * @return      The information of the created user entry.
     */
    ContactDTO create(ContactDTO contact);

    /**
     * Deletes a user entry.
     * @param sid    The id of the deleted user entry.
     * @return      The information of the deleted user entry.
     * @throws      com.ethan.morephone.api.phonenumber.PhoneNumberNotFoundException if no user entry is found.
     */
    ContactDTO delete(String sid);

    /**
     * Finds all user entries.
     * @return      The information of all user entries.
     */
    List<ContactDTO> findAll();

    /**
     * Finds a single user entry.
     * @param id    The id of the requested user entry.
     * @return      The information of the requested user entry.
     * @throws      com.ethan.morephone.api.phonenumber.PhoneNumberNotFoundException if no user entry is found.
     */
    ContactDTO findById(String id);

    /**
     * Updates the information of a user entry.
     * @param user  The information of the updated user entry.
     * @return      The information of the updated user entry.
     * @throws      com.ethan.morephone.api.phonenumber.PhoneNumberNotFoundException if no user entry is found.
     */
    ContactDTO update(ContactDTO user);

    List<ContactDTO> findByPhoneNumberId(String phoneNumberId);

    List<ContactDTO> findByUserId(String userId);

    ContactDTO findByPhoneNumber(String phoneNumber);
}
