package com.ethan.morephone.api.usage.repository;

import com.ethan.morephone.api.usage.domain.Usage;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Created by truongnguyen on 7/15/17.
 */
public interface UsageRepository extends Repository<Usage, String> {

    /**
     * Deletes a usage entry from the database.
     *
     * @param deleted The deleted usage entry.
     */
    void delete(Usage deleted);

    /**
     * Finds all usage entries from the database.
     *
     * @return The information of all usage entries that are found from the database.
     */
    List<Usage> findAll();

    /**
     * Finds the information of a single usage entry.
     *
     * @param id The id of the requested usage entry.
     * @return The information of the found usage entry. If no usage entry
     * is found, this method returns an empty {@link Optional} object.
     */
    Optional<Usage> findOne(String id);

    /**
     * Saves a new usage entry to the database.
     *
     * @param saved The information of the saved usage entry.
     * @return The information of the saved usage entry.
     */
    Usage save(Usage saved);

    List<Usage> findByUserId(String userId);

    List<Usage> findByAccountSid(String accountSid);

}
