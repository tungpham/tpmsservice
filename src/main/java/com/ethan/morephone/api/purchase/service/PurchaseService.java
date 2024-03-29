package com.ethan.morephone.api.purchase.service;


import com.ethan.morephone.api.purchase.domain.PurchaseDTO;

import java.util.List;

/**
 * Created by truongnguyen on 7/15/17.
 */
public interface PurchaseService {

    /**
     * Creates a new user entry.
     * @param user  The information of the created user entry.
     * @return      The information of the created user entry.
     */
    PurchaseDTO create(PurchaseDTO user);

    /**
     * Deletes a user entry.
     * @param id    The id of the deleted user entry.
     * @return      The information of the deleted user entry.
     * @throws      com.ethan.tracking.UserNotFoundException if no user entry is found.
     */
    PurchaseDTO delete(String id);

    /**
     * Finds all user entries.
     * @return      The information of all user entries.
     */
    List<PurchaseDTO> findAll();

    /**
     * Finds a single user entry.
     * @param id    The id of the requested user entry.
     * @return      The information of the requested user entry.
     * @throws      com.ethan.tracking.UserNotFoundException if no user entry is found.
     */
    PurchaseDTO findById(String id);

    /**
     * Updates the information of a user entry.
     * @param user  The information of the updated user entry.
     * @return      The information of the updated user entry.
     * @throws      com.ethan.tracking.UserNotFoundException if no user entry is found.
     */
    PurchaseDTO update(PurchaseDTO user);
}
