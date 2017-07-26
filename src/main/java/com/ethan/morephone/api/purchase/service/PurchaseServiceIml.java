package com.ethan.morephone.api.purchase.service;

import com.ethan.morephone.api.purchase.domain.Purchase;
import com.ethan.morephone.api.purchase.domain.PurchaseDTO;
import com.ethan.morephone.api.purchase.repository.PurchaseRepository;
import com.ethan.morephone.api.user.UserNotFoundException;
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
public class PurchaseServiceIml implements PurchaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PurchaseServiceIml.class);

    private final PurchaseRepository repository;

    @Autowired
    PurchaseServiceIml(PurchaseRepository repository) {
        this.repository = repository;
    }

    @Override
    public PurchaseDTO create(PurchaseDTO user) {
        LOGGER.info("Creating a new user entry with information: {}", user);

        Purchase persisted = Purchase.getBuilder()
                .email(user.getEmail())
                .packageName(user.getPackageName())
                .orderId(user.getOrderId())
                .purchaseState(user.getPurchaseState())
                .purchaseTime(user.getPurchaseTime())
                .productId(user.getProductId())
                .token(user.getToken())
                .build();

        persisted = repository.save(persisted);
        LOGGER.info("Created a new user entry with information: {}", persisted);

        return convertToDTO(persisted);
    }

    @Override
    public PurchaseDTO delete(String id) {
        LOGGER.info("Deleting a user entry with id: {}", id);

        Purchase deleted = findTodoById(id);
        repository.delete(deleted);

        LOGGER.info("Deleted user entry with information: {}", deleted);

        return convertToDTO(deleted);
    }

    @Override
    public List<PurchaseDTO> findAll() {
        LOGGER.info("Finding all user entries.");

        List<Purchase> users = repository.findAll();

        LOGGER.info("Found {} user entries", users.size());

        return convertToDTOs(users);
    }

    @Override
    public PurchaseDTO findById(String id) {
        LOGGER.info("Finding user entry with id: {}", id);

        Purchase found = findTodoById(id);

        LOGGER.info("Found user entry: {}", found);

        return convertToDTO(found);
    }

    @Override
    public PurchaseDTO update(PurchaseDTO user) {
        LOGGER.info("Updating user entry with information: {}", user);

        Purchase updated = findTodoById(user.getId());
//        updated.update(user.getCountry(), user.getLanguageCode());
//        updated = repository.save(updated);

        LOGGER.info("Updated user entry with information: {}", updated);

        return convertToDTO(updated);
    }

    private Purchase findTodoById(String id) {
        Optional<Purchase> result = repository.findOne(id);
        return result.orElseThrow(() -> new UserNotFoundException(id));

    }

    private List<PurchaseDTO> convertToDTOs(List<Purchase> models) {
        return models.stream()
                .map(this::convertToDTO)
                .collect(toList());
    }


    private PurchaseDTO convertToDTO(Purchase model) {
        PurchaseDTO dto = new PurchaseDTO();

        dto.setId(model.getId());
        dto.setEmail(model.getEmail());
        dto.setPackageName(model.getPackageName());
        dto.setToken(model.getToken());
        dto.setPurchaseState(model.getPurchaseState());
        dto.setOrderId(model.getOrderId());
        dto.setPurchaseTime(model.getPurchaseTime());
        dto.setProductId(model.getProductId());
        dto.setCreatedAt(model.getCreatedAt());
        dto.setUpdatedAt(model.getUpdatedAt());


        return dto;
    }
}
