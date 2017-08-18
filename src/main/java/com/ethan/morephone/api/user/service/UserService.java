package com.ethan.morephone.api.user.service;


import com.ethan.morephone.api.user.domain.UserDTO;

import java.util.List;

/**
 * Created by truongnguyen on 7/15/17.
 */
public interface UserService {

    /**
     * Creates a new user entry.
     * @param user  The information of the created user entry.
     * @return      The information of the created user entry.
     */
    UserDTO create(UserDTO user);

    /**
     * Deletes a user entry.
     * @param id    The id of the deleted user entry.
     * @return      The information of the deleted user entry.
     * @throws      com.ethan.morephone.api.user.UserNotFoundException if no user entry is found.
     */
    UserDTO delete(String id);

    /**
     * Finds all user entries.
     * @return      The information of all user entries.
     */
    List<UserDTO> findAll();

    /**
     * Finds a single user entry.
     * @param id    The id of the requested user entry.
     * @return      The information of the requested user entry.
     * @throws      com.ethan.morephone.api.user.UserNotFoundException if no user entry is found.
     */
    UserDTO findById(String id);

    /**
     * Updates the information of a user entry.
     * @param user  The information of the updated user entry.
     * @return      The information of the updated user entry.
     * @throws      com.ethan.morephone.api.user.UserNotFoundException if no user entry is found.
     */
    UserDTO update(UserDTO user);

    UserDTO findByEmail(String email);

    UserDTO findByAccountSid(String accountSid);

    UserDTO updateToken(String id, String token);

}
