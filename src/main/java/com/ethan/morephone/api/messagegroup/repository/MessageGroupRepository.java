package com.ethan.morephone.api.messagegroup.repository;

import com.ethan.morephone.api.messagegroup.domain.MessageGroup;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Created by truongnguyen on 9/28/17.
 */
public interface MessageGroupRepository extends Repository<MessageGroup, String> {

    /**
     * Deletes a user entry from the database.
     *
     * @param deleted The deleted user entry.
     */
    void delete(MessageGroup deleted);

    /**
     * Finds all user entries from the database.
     *
     * @return The information of all user entries that are found from the database.
     */
    List<MessageGroup> findAll();

    /**
     * Finds the information of a single user entry.
     *
     * @param id The id of the requested user entry.
     * @return The information of the found user entry. If no user entry
     * is found, this method returns an empty {@link Optional} object.
     */
    Optional<MessageGroup> findOne(String id);

    /**
     * Saves a new user entry to the database.
     *
     * @param saved The information of the saved user entry.
     * @return The information of the saved user entry.
     */
    MessageGroup save(MessageGroup saved);

    List<MessageGroup> findByMessageSid(String sid);

    List<MessageGroup> findById(String id);

    List<MessageGroup> findByUserId(String userId);

    List<MessageGroup> findByPhoneNumberId(String phoneNumberId);

}
