package com.ethan.morephone.api.phonenumber.service;

import com.ethan.morephone.api.phonenumber.PhoneNumberNotFoundException;
import com.ethan.morephone.api.phonenumber.domain.PhoneNumber;
import com.ethan.morephone.api.phonenumber.domain.PhoneNumberDTO;
import com.ethan.morephone.api.phonenumber.repository.PhoneNumberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

/**
 * Created by truongnguyen on 7/15/17.
 */
@Service
public class PhoneNumberServiceIml implements PhoneNumberService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PhoneNumberServiceIml.class);

    private final PhoneNumberRepository repository;

    @Autowired
    PhoneNumberServiceIml(PhoneNumberRepository repository) {
        this.repository = repository;
    }

    @Override
    public PhoneNumberDTO create(PhoneNumberDTO user) {
        LOGGER.info("Creating a new user entry with information: {}", user);

        PhoneNumber persisted = PhoneNumber.getBuilder()
                .sid(user.getSid())
                .friendlyName(user.getFriendlyName())
                .phoneNumber(user.getPhoneNumber())
                .userId(user.getUserId())
                .build();

        persisted = repository.save(persisted);
        LOGGER.info("Created a new user entry with information: {}", persisted);

        return convertToDTO(persisted);
    }

    @Override
    public PhoneNumberDTO delete(String id) {
        LOGGER.info("Deleting a user entry with id: {}", id);

        PhoneNumber deleted = findPhoneNumberById(id);
        repository.delete(deleted);

        LOGGER.info("Deleted user entry with information: {}", deleted);

        return convertToDTO(deleted);
    }

    @Override
    public List<PhoneNumberDTO> findAll() {
        LOGGER.info("Finding all user entries.");

        List<PhoneNumber> users = repository.findAll();

        LOGGER.info("Found {} user entries", users.size());

        return convertToDTOs(users);
    }

    @Override
    public PhoneNumberDTO findById(String id) {
        LOGGER.info("Finding user entry with id: {}", id);

        PhoneNumber found = findPhoneNumberById(id);

        LOGGER.info("Found user entry: {}", found);

        return convertToDTO(found);
    }

    @Override
    public PhoneNumberDTO update(PhoneNumberDTO user) {
        LOGGER.info("Updating user entry with information: {}", user);

        PhoneNumber updated = findPhoneNumberById(user.getId());
        updated = repository.save(updated);

        LOGGER.info("Updated user entry with information: {}", updated);

        return convertToDTO(updated);
    }

    @Override
    public PhoneNumberDTO findBySid(String sid) {
        PhoneNumber found = findPhoneNumberBySid(sid);
        if (found != null) {
            return convertToDTO(found);
        } else {
            return null;
        }
    }

    @Override
    public PhoneNumberDTO findByPhoneNumber(String phoneNumber) {
        PhoneNumber found = findPhoneNumberByPhoneNumber(phoneNumber);
        if (found != null) {
            return convertToDTO(found);
        } else {
            return null;
        }
    }

    private PhoneNumber findPhoneNumberById(String id) {
        Optional<PhoneNumber> result = repository.findOne(id);
        return result.orElseThrow(() -> new PhoneNumberNotFoundException(id));

    }

    private PhoneNumber findPhoneNumberByPhoneNumber(String phoneNumber) {
        List<PhoneNumber> result = repository.findByPhoneNumber(phoneNumber);
        if (result != null && !result.isEmpty()) {
            return result.get(0);
        } else {
            return null;
        }
    }

    private PhoneNumber findPhoneNumberBySid(String sid) {
        List<PhoneNumber> result = repository.findBySid(sid);
        if (result != null && !result.isEmpty()) {
            return result.get(0);
        } else {
            return null;
        }
    }


    private List<PhoneNumberDTO> convertToDTOs(List<PhoneNumber> models) {
        return models.stream()
                .map(this::convertToDTO)
                .collect(toList());
    }


    private PhoneNumberDTO convertToDTO(PhoneNumber model) {
        PhoneNumberDTO dto = new PhoneNumberDTO();

        dto.setId(model.getId());
        dto.setSid(model.getSid());
        dto.setFriendlyName(model.getFriendlyName());
        dto.setPhoneNumber(model.getPhoneNumber());
        dto.setUserId(model.getUserId());
        dto.setCreatedAt(model.getCreatedAt());
        dto.setUpdatedAt(model.getUpdatedAt());


        return dto;
    }
}
