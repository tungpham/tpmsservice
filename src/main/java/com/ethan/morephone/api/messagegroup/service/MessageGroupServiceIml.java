package com.ethan.morephone.api.messagegroup.service;

import com.ethan.morephone.api.messagegroup.domain.MessageGroup;
import com.ethan.morephone.api.messagegroup.domain.MessageGroupDTO;
import com.ethan.morephone.api.messagegroup.repository.MessageGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Created by truongnguyen on 9/28/17.
 */
@Service("messageGroupService")
public class MessageGroupServiceIml implements MessageGroupService {

    private final MessageGroupRepository repository;
    private HashMap<String, MessageGroupDTO> mMessageGroupHashMap = new HashMap<>();

    @Autowired
    MessageGroupServiceIml(MessageGroupRepository repository) {
        this.repository = repository;
    }

    @Override
    public MessageGroupDTO create(MessageGroupDTO contact) {
        MessageGroup persisted = MessageGroup.getBuilder()
                .messageSid(contact.getMessageSid())
                .userId(contact.getUserId())
                .phoneNumberId(contact.getPhoneNumberId())
                .dateSent(contact.getDateSent())
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
                    .messageSid(contact.getMessageSid())
                    .userId(contact.getUserId())
                    .phoneNumberId(contact.getPhoneNumberId())
                    .groupId(contact.getGroupId())
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
        List<MessageGroup> result = repository.findByUserId(userId);
        if(result != null && !result.isEmpty()){
            return convertToDTOs(result);
        }
        return null;
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
        dto.setDateSent(model.getDateSent());
        dto.setMessageSid(model.getMessageSid());
        dto.setUserId(model.getUserId());
        dto.setPhoneNumberId(model.getPhoneNumberId());
        dto.setCreatedAt(model.getCreatedAt());
        dto.setUpdatedAt(model.getUpdatedAt());

        mMessageGroupHashMap.put(model.getMessageSid(), dto);

        return dto;
    }

    public HashMap<String, MessageGroupDTO> getMessageGroupHashMap(){
        return mMessageGroupHashMap;
    }
}
