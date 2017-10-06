package com.ethan.morephone.api.messagegroup.service;

import com.ethan.morephone.api.messagegroup.domain.MessageGroup;
import com.ethan.morephone.api.messagegroup.domain.MessageGroupDTO;
import com.ethan.morephone.api.messagegroup.repository.MessageGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Created by truongnguyen on 9/28/17.
 */
@Service("messageGroupService")
public class MessageGroupServiceIml implements MessageGroupService {

    private final MessageGroupRepository repository;

    @Autowired
    MessageGroupServiceIml(MessageGroupRepository repository) {
        this.repository = repository;
    }

    @Override
    public MessageGroupDTO create(MessageGroupDTO contact) {
        MessageGroup persisted = MessageGroup.getBuilder()
                .name(contact.getName())
                .groupPhone(contact.getGroupPhone())
                .userId(contact.getUserId())
                .phoneNumberId(contact.getPhoneNumberId())
                .build();

        persisted = repository.save(persisted);

        return convertToDTO(persisted);
    }

    @Override
    public MessageGroupDTO delete(String id) {
        MessageGroup deleted = findContactById(id);
        if (deleted != null) {
            repository.delete(deleted);
            return convertToDTO(deleted);
        } else {
            return null;
        }
    }

    @Override
    public List<MessageGroupDTO> findAll() {
        return null;
    }

    @Override
    public MessageGroupDTO findById(String id) {
        MessageGroup contact = findContactById(id);
        if (contact != null) {
            return convertToDTO(contact);
        } else {
            return null;
        }
    }

    @Override
    public MessageGroupDTO update(MessageGroupDTO contact) {
        List<MessageGroup> result = repository.findById(contact.getId());
        if (result != null && !result.isEmpty()) {
            MessageGroup updated = MessageGroup.getBuilder()
                    .id(contact.getId())
                    .name(contact.getName())
                    .groupPhone(contact.getGroupPhone())
                    .userId(contact.getUserId())
                    .phoneNumberId(contact.getPhoneNumberId())
                    .build();
            updated = repository.save(updated);
            return convertToDTO(updated);
        } else {
            return null;
        }
    }

    @Override
    public List<MessageGroupDTO> findByPhoneNumberId(String phoneNumberId) {
        List<MessageGroup> result = repository.findByPhoneNumberId(phoneNumberId);
        if(result != null && !result.isEmpty()){
            return convertToDTOs(result);
        }
        return null;
    }

    @Override
    public List<MessageGroupDTO> findByUserId(String userId) {
        return null;
    }

    @Override
    public MessageGroupDTO findByPhoneNumber(String phoneNumber) {

        List<MessageGroup> result = repository.findByPhoneNumber(phoneNumber);
        if (result != null && !result.isEmpty()) {
            return convertToDTO(result.get(0));
        } else {
            return null;
        }
    }

    private MessageGroup findContactById(String sid) {
        List<MessageGroup> result = repository.findById(sid);
        if (result != null && !result.isEmpty()) {
            return result.get(0);
        } else {
            return null;
        }
    }


    private List<MessageGroupDTO> convertToDTOs(List<MessageGroup> models) {
        return models.stream()
                .map(this::convertToDTO)
                .collect(toList());
    }


    private MessageGroupDTO convertToDTO(MessageGroup model) {
        MessageGroupDTO dto = new MessageGroupDTO();

        dto.setId(model.getId());
        dto.setGroupPhone(model.getGroupPhone());
        dto.setName(model.getName());
        dto.setUserId(model.getUserId());
        dto.setPhoneNumberId(model.getPhoneNumberId());
        dto.setCreatedAt(model.getCreatedAt());
        dto.setUpdatedAt(model.getUpdatedAt());

        return dto;
    }
}
