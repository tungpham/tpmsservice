package com.ethan.morephone.api.group.repository;

import com.ethan.morephone.api.group.domain.Group;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Created by truongnguyen on 9/28/17.
 */
public interface GroupRepository extends Repository<Group, String> {

    /**
     * Deletes a user entry from the database.
     *
     * @param deleted The deleted user entry.
     */
    void delete(Group deleted);

    /**
     * Finds all user entries from the database.
     *
     * @return The information of all user entries that are found from the database.
     */
    List<Group> findAll();

    /**
     * Finds the information of a single user entry.
     *
     * @param id The id of the requested user entry.
     * @return The information of the found user entry. If no user entry
     * is found, this method returns an empty {@link Optional} object.
     */
    Optional<Group> findOne(String id);

    /**
     * Saves a new user entry to the database.
     *
     * @param saved The information of the saved user entry.
     * @return The information of the saved user entry.
     */
    Group save(Group saved);

    List<Group> findById(String id);

    List<Group> findByUserId(String userId);

    List<Group> findByPhoneNumberId(String phoneNumberId);

}
