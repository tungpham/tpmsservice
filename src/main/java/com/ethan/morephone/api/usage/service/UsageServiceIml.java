package com.ethan.morephone.api.usage.service;

import com.ethan.morephone.api.usage.domain.Usage;
import com.ethan.morephone.api.usage.domain.UsageDTO;
import com.ethan.morephone.api.usage.repository.UsageRepository;
import com.ethan.morephone.api.user.UserNotFoundException;
import com.ethan.morephone.utils.Utils;
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
public class UsageServiceIml implements UsageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UsageServiceIml.class);

    private final UsageRepository repository;

    @Autowired
    UsageServiceIml(UsageRepository repository) {
        this.repository = repository;
    }

    @Override
    public UsageDTO create(UsageDTO user) {
        LOGGER.info("Creating a new user entry with information: {}", user);

        Usage persisted = Usage.getBuilder()
                .userId(user.getUserId())
                .accountSid(user.getAccountSid())
                .balance(user.getBalance())
                .messageIncoming(user.getMessageIncoming())
                .messageOutgoing(user.getMessageOutgoing())
                .callIncoming(user.getCallIncoming())
                .callOutgoing(user.getCallOutgoing())
                .build();

        persisted = repository.save(persisted);
        LOGGER.info("Created a new user entry with information: {}", persisted);

        return convertToDTO(persisted);
    }

    @Override
    public UsageDTO delete(String id) {
        LOGGER.info("Deleting a user entry with id: {}", id);

        Usage deleted = findUsageById(id);
        repository.delete(deleted);

        LOGGER.info("Deleted user entry with information: {}", deleted);

        return convertToDTO(deleted);
    }

    @Override
    public List<UsageDTO> findAll() {
        LOGGER.info("Finding all user entries.");

        List<Usage> users = repository.findAll();

        LOGGER.info("Found {} user entries", users.size());

        return convertToDTOs(users);
    }

    @Override
    public UsageDTO findById(String id) {

        Utils.logMessage("Finding user entry with id: " + id);

        Usage found = findUsageById(id);

        Utils.logMessage("Found user entry with id: " + found.toString());

        return convertToDTO(found);
    }

    @Override
    public UsageDTO findByUserId(String userId) {
        Usage usage = findUsageByUserId(userId);
        if (usage != null) {
            return convertToDTO(usage);
        } else {
            return null;
        }
    }

    @Override
    public UsageDTO findByAccountSid(String accountSid) {
        Usage usage = findUsageByAccountSid(accountSid);
        if (usage != null) {
            return convertToDTO(usage);
        } else {
            return null;
        }
    }

    @Override
    public UsageDTO updateBalance(String userId, double balance) {
        Usage usage = findUsageByUserId(userId);
        if (usage != null) {
            usage.updateBalance(balance);
            return convertToDTO(repository.save(usage));
        }
        return null;
    }

    @Override
    public UsageDTO updateMessageIncoming(String userId) {
        Usage usage = findUsageByUserId(userId);
        if (usage != null) {
            usage.updateMessageIncoming();
            return convertToDTO(repository.save(usage));
        }
        return null;
    }

    @Override
    public UsageDTO updateMessageOutgoing(String userId) {
        Usage usage = findUsageByUserId(userId);
        if (usage != null) {
            usage.updateMessageOutgoing();
            return convertToDTO(repository.save(usage));
        }
        return null;
    }

    @Override
    public UsageDTO updateCallIncoming(String userId, double balance) {
        Usage usage = findUsageByUserId(userId);
        if (usage != null) {
            usage.updateCallIncoming(balance);
            return convertToDTO(repository.save(usage));
        }
        return null;
    }

    @Override
    public UsageDTO updateCallOutgoing(String userId, double balance) {
        Usage usage = findUsageByUserId(userId);
        if (usage != null) {
            usage.updateCallOutgoing(balance);
            return convertToDTO(repository.save(usage));
        }
        return null;
    }


    private Usage findUsageByUserId(String userId) {
        List<Usage> result = repository.findByUserId(userId);
        if (result != null && !result.isEmpty()) {
            return result.get(0);
        } else {
            return null;
        }
    }

    private Usage findUsageByAccountSid(String accountSid) {
        List<Usage> result = repository.findByAccountSid(accountSid);
        if (result != null && !result.isEmpty()) {
            return result.get(0);
        } else {
            return null;
        }
    }


    private Usage findUsageById(String id) {
        Optional<Usage> result = repository.findOne(id);
        return result.orElseThrow(() -> new UserNotFoundException(id));

    }

    private List<UsageDTO> convertToDTOs(List<Usage> models) {
        return models.stream()
                .map(this::convertToDTO)
                .collect(toList());
    }


    private UsageDTO convertToDTO(Usage model) {
        UsageDTO dto = new UsageDTO();

        dto.setId(model.getId());
        dto.setUserId(model.getUserId());
        dto.setAccountSid(model.getAccountSid());
        dto.setBalance(model.getBalance());
        dto.setMessageIncoming(model.getMessageIncoming());
        dto.setMessageOutgoing(model.getMessageOutgoing());
        dto.setCallIncoming(model.getCallIncoming());
        dto.setCallOutgoing(model.getCallOutgoing());
        dto.setCreatedAt(model.getCreatedAt());
        dto.setUpdatedAt(model.getUpdatedAt());

        return dto;
    }
}
