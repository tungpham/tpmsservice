package com.ethan.morephone.api.usage.service;


import com.ethan.morephone.api.usage.domain.UsageDTO;

import java.util.List;

/**
 * Created by truongnguyen on 7/15/17.
 */
public interface UsageService {

    /**
     * Creates a new usage entry.
     * @param user  The information of the created usage entry.
     * @return      The information of the created usage entry.
     */
    UsageDTO create(UsageDTO user);

    /**
     * Deletes a usage entry.
     * @param id    The id of the deleted usage entry.
     * @return      The information of the deleted usage entry.
     * @throws      com.ethan.morephone.api.user.UserNotFoundException if no usage entry is found.
     */
    UsageDTO delete(String id);

    /**
     * Finds all usage entries.
     * @return      The information of all usage entries.
     */
    List<UsageDTO> findAll();

    /**
     * Finds a single usage entry.
     * @param id    The id of the requested usage entry.
     * @return      The information of the requested usage entry.
     * @throws      com.ethan.morephone.api.user.UserNotFoundException if no usage entry is found.
     */
    UsageDTO findById(String id);

    UsageDTO findByUserId(String id);

    UsageDTO findByAccountSid(String accountSid);

    /**
     * Updates the information of a usage entry.
     * @param userId  The information of the updated usage entry.
     * @return      The information of the updated usage entry.
     * @throws      com.ethan.morephone.api.user.UserNotFoundException if no usage entry is found.
     */
    UsageDTO updateBalance(String userId, double balance);

    UsageDTO updateMessageIncoming(String userId);

    UsageDTO updateMessageOutgoing(String userId);

    UsageDTO updateCallIncoming(String userId, double balance);

    UsageDTO updateCallOutgoing(String userId, double balance);

}
