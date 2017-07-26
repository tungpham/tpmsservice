package com.ethan.morephone.api.user.repository;

import com.ethan.morephone.api.user.domain.User;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Created by truongnguyen on 7/15/17.
 */
public interface UserRepository extends Repository<User, String> {

    /**
     * Deletes a user entry from the database.
     *
     * @param deleted The deleted user entry.
     */
    void delete(User deleted);

    /**
     * Finds all user entries from the database.
     *
     * @return The information of all user entries that are found from the database.
     */
    List<User> findAll();

    /**
     * Finds the information of a single user entry.
     *
     * @param id The id of the requested user entry.
     * @return The information of the found user entry. If no user entry
     * is found, this method returns an empty {@link Optional} object.
     */
    Optional<User> findOne(String id);

    /**
     * Saves a new user entry to the database.
     *
     * @param saved The information of the saved user entry.
     * @return The information of the saved user entry.
     */
    User save(User saved);

    List<User> findByEmail(String email);
}
