package com.ethan.morephone.api.purchase.repository;

import com.ethan.morephone.api.purchase.domain.Purchase;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Created by truongnguyen on 7/15/17.
 */
public interface PurchaseRepository extends Repository<Purchase, String> {

    /**
     * Deletes a purchase entry from the database.
     *
     * @param deleted The deleted purchase entry.
     */
    void delete(Purchase deleted);

    /**
     * Finds all purchase entries from the database.
     *
     * @return The information of all purchase entries that are found from the database.
     */
    List<Purchase> findAll();

    /**
     * Finds the information of a single purchase entry.
     *
     * @param id The id of the requested purchase entry.
     * @return The information of the found purchase entry. If no purchase entry
     * is found, this method returns an empty {@link Optional} object.
     */
    Optional<Purchase> findOne(String id);

    /**
     * Saves a new purchase entry to the database.
     *
     * @param saved The information of the saved purchase entry.
     * @return The information of the saved purchase entry.
     */
    Purchase save(Purchase saved);
}
