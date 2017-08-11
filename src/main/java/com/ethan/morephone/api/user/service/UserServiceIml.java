package com.ethan.morephone.api.user.service;

import com.ethan.morephone.api.user.UserNotFoundException;
import com.ethan.morephone.api.user.domain.User;
import com.ethan.morephone.api.user.domain.UserDTO;
import com.ethan.morephone.api.user.repository.UserRepository;
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
public class UserServiceIml implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceIml.class);

    private final UserRepository repository;

    @Autowired
    UserServiceIml(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDTO create(UserDTO user) {
        LOGGER.info("Creating a new user entry with information: {}", user);

        User persisted = User.getBuilder()
                .email(user.getEmail())
                .accountSid(user.getAccountSid())
                .country(user.getCountry())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .languageCode(user.getLanguageCode())
                .device(user.getDevice())
                .token(user.getToken())
                .platform(user.getPlatform())
                .build();

        persisted = repository.save(persisted);
        LOGGER.info("Created a new user entry with information: {}", persisted);

        return convertToDTO(persisted);
    }

    @Override
    public UserDTO delete(String id) {
        LOGGER.info("Deleting a user entry with id: {}", id);

        User deleted = findTodoById(id);
        repository.delete(deleted);

        LOGGER.info("Deleted user entry with information: {}", deleted);

        return convertToDTO(deleted);
    }

    @Override
    public List<UserDTO> findAll() {
        LOGGER.info("Finding all user entries.");

        List<User> users = repository.findAll();

        LOGGER.info("Found {} user entries", users.size());

        return convertToDTOs(users);
    }

    @Override
    public UserDTO findById(String id) {

        Utils.logMessage("Finding user entry with id: " + id);

        User found = findTodoById(id);

        Utils.logMessage("Found user entry with id: " + found.toString());

        return convertToDTO(found);
    }

    @Override
    public UserDTO update(UserDTO user) {
        LOGGER.info("Updating user entry with information: {}", user);

        User updated = findTodoById(user.getId());
        updated.update(user.getToken());
        updated = repository.save(updated);

        LOGGER.info("Updated user entry with information: {}", updated);

        return convertToDTO(updated);
    }

    @Override
    public UserDTO findByEmail(String email) {
        User found = findUserByEmail(email);
        if (found != null) {
            return convertToDTO(found);
        } else {
            return null;
        }
    }

    @Override
    public UserDTO findByAccountSid(String accountSid) {
        User found = findUserByAccountSid(accountSid);
        if (found != null) {
            return convertToDTO(found);
        } else {
            return null;
        }
    }

    @Override
    public UserDTO updateToken(String id, String token) {
        User updated = findTodoById(id);
        if(!updated.getToken().equals(token)) {
            updated.update(token);
            updated = repository.save(updated);
        }
        return convertToDTO(updated);
    }

    private User findTodoById(String id) {
        Optional<User> result = repository.findOne(id);
        return result.orElseThrow(() -> new UserNotFoundException(id));

    }

    private User findUserByEmail(String email) {
        List<User> result = repository.findByEmail(email);
        if (result != null && !result.isEmpty()) {
            return result.get(0);
        } else {
            return null;
        }

    }

    private User findUserByAccountSid(String accountSid) {
        List<User> result = repository.findByAccountSid(accountSid);
        if (result != null && !result.isEmpty()) {
            return result.get(0);
        } else {
            return null;
        }
    }

    private List<UserDTO> convertToDTOs(List<User> models) {
        return models.stream()
                .map(this::convertToDTO)
                .collect(toList());
    }


    private UserDTO convertToDTO(User model) {
        UserDTO dto = new UserDTO();

        dto.setId(model.getId());
        dto.setAccountSid(model.getAccountSid());
        dto.setEmail(model.getEmail());
        dto.setCountry(model.getCountry());
        dto.setFirstName(model.getFirstName());
        dto.setLastName(model.getLastName());
        dto.setToken(model.getToken());
        dto.setCreatedAt(model.getCreatedAt());
        dto.setUpdatedAt(model.getUpdatedAt());
        dto.setPlatform(model.getPlatform());
        dto.setDevice(model.getDevice());
        dto.setLanguageCode(model.getLanguageCode());

        return dto;
    }
}
