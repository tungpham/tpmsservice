package com.ethan.morephone.api.group.service;

import com.ethan.morephone.api.group.domain.Group;
import com.ethan.morephone.api.group.domain.GroupDTO;
import com.ethan.morephone.api.group.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Created by truongnguyen on 9/28/17.
 */
@Service("groupService")
public class GroupServiceIml implements GroupService {

    private final GroupRepository repository;

    @Autowired
    GroupServiceIml(GroupRepository repository) {
        this.repository = repository;
    }

    @Override
    public GroupDTO create(GroupDTO contact) {
        Group persisted = Group.getBuilder()
                .name(contact.getName())
                .groupPhone(contact.getGroupPhone())
                .userId(contact.getUserId())
                .phoneNumberId(contact.getPhoneNumberId())
                .build();

        persisted = repository.save(persisted);

        return convertToDTO(persisted);
    }

    @Override
    public GroupDTO delete(String id) {
        Group deleted = findContactById(id);
        if (deleted != null) {
            repository.delete(deleted);
            return convertToDTO(deleted);
        } else {
            return null;
        }
    }

    @Override
    public List<GroupDTO> findAll() {
        return null;
    }

    @Override
    public GroupDTO findById(String id) {
        Group contact = findContactById(id);
        if (contact != null) {
            return convertToDTO(contact);
        } else {
            return null;
        }
    }

    @Override
    public GroupDTO update(GroupDTO contact) {
        List<Group> result = repository.findById(contact.getId());
        if (result != null && !result.isEmpty()) {
            Group updated = Group.getBuilder()
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
    public List<GroupDTO> findByPhoneNumberId(String phoneNumberId) {
        List<Group> result = repository.findByPhoneNumberId(phoneNumberId);
        if(result != null && !result.isEmpty()){
            return convertToDTOs(result);
        }
        return null;
    }

    @Override
    public List<GroupDTO> findByUserId(String userId) {
        List<Group> result = repository.findByUserId(userId);
        if(result != null && !result.isEmpty()){
            return convertToDTOs(result);
        }
        return null;
    }


    private Group findContactById(String sid) {
        List<Group> result = repository.findById(sid);
        if (result != null && !result.isEmpty()) {
            return result.get(0);
        } else {
            return null;
        }
    }


    private List<GroupDTO> convertToDTOs(List<Group> models) {
        return models.stream()
                .map(this::convertToDTO)
                .collect(toList());
    }


    private GroupDTO convertToDTO(Group model) {
        GroupDTO dto = new GroupDTO();

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
