package com.ethan.morephone.api.contact.repository;

import com.ethan.morephone.api.contact.domain.Contact;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Created by truongnguyen on 9/28/17.
 */
public interface ContactRepository extends Repository<Contact, String> {

    /**
     * Deletes a user entry from the database.
     *
     * @param deleted The deleted user entry.
     */
    void delete(Contact deleted);

    /**
     * Finds all user entries from the database.
     *
     * @return The information of all user entries that are found from the database.
     */
    List<Contact> findAll();

    /**
     * Finds the information of a single user entry.
     *
     * @param id The id of the requested user entry.
     * @return The information of the found user entry. If no user entry
     * is found, this method returns an empty {@link Optional} object.
     */
    Optional<Contact> findOne(String id);

    /**
     * Saves a new user entry to the database.
     *
     * @param saved The information of the saved user entry.
     * @return The information of the saved user entry.
     */
    Contact save(Contact saved);

    List<Contact> findById(String id);

    List<Contact> findByUserId(String userId);

    List<Contact> findByPhoneNumberId(String phoneNumberId);

    List<Contact> findByPhoneNumber(String phoneNumber);
}
