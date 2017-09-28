package com.ethan.morephone.api.contact.service;

import com.ethan.morephone.api.contact.domain.Contact;
import com.ethan.morephone.api.contact.domain.ContactDTO;
import com.ethan.morephone.api.contact.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Created by truongnguyen on 9/28/17.
 */
@Service("contactService")
public class ContactServiceIml implements ContactService {

    private final ContactRepository repository;

    @Autowired
    ContactServiceIml(ContactRepository repository) {
        this.repository = repository;
    }

    @Override
    public ContactDTO create(ContactDTO contact) {
        Contact persisted = Contact.getBuilder()
                .id(contact.getId())
                .displayName(contact.getDisplayName())
                .phoneNumber(contact.getPhoneNumber())
                .userId(contact.getUserId())
                .photoUri(contact.getPhotoUri())
                .phoneNumberId(contact.getPhoneNumberId())
                .address(contact.getAddress())
                .email(contact.getEmail())
                .note(contact.getNote())
                .build();

        persisted = repository.save(persisted);

        return convertToDTO(persisted);
    }

    @Override
    public ContactDTO delete(String id) {
        Contact deleted = findContactById(id);
        if (deleted != null) {
            repository.delete(deleted);
            return convertToDTO(deleted);
        } else {
            return null;
        }
    }

    @Override
    public List<ContactDTO> findAll() {
        return null;
    }

    @Override
    public ContactDTO findById(String id) {
        Contact contact = findContactById(id);
        if (contact != null) {
            return convertToDTO(contact);
        } else {
            return null;
        }
    }

    @Override
    public ContactDTO update(ContactDTO contact) {
        List<Contact> result = repository.findById(contact.getId());
        if (result != null && !result.isEmpty()) {
            Contact updated = Contact.getBuilder()
                    .id(contact.getId())
                    .displayName(contact.getDisplayName())
                    .phoneNumber(contact.getPhoneNumber())
                    .userId(contact.getUserId())
                    .photoUri(contact.getPhotoUri())
                    .phoneNumberId(contact.getPhoneNumberId())
                    .address(contact.getAddress())
                    .email(contact.getEmail())
                    .note(contact.getNote())
                    .build();
            updated = repository.save(updated);
            return convertToDTO(updated);
        } else {
            return null;
        }
    }

    @Override
    public List<ContactDTO> findByPhoneNumberId(String phoneNumberId) {
        List<Contact> result = repository.findByPhoneNumberId(phoneNumberId);
        if(result != null && !result.isEmpty()){
            return convertToDTOs(result);
        }
        return null;
    }

    @Override
    public List<ContactDTO> findByUserId(String userId) {
        return null;
    }

    @Override
    public ContactDTO findByPhoneNumber(String phoneNumber) {

        List<Contact> result = repository.findByPhoneNumber(phoneNumber);
        if (result != null && !result.isEmpty()) {
            return convertToDTO(result.get(0));
        } else {
            return null;
        }
    }

    private Contact findContactById(String sid) {
        List<Contact> result = repository.findById(sid);
        if (result != null && !result.isEmpty()) {
            return result.get(0);
        } else {
            return null;
        }
    }


    private List<ContactDTO> convertToDTOs(List<Contact> models) {
        return models.stream()
                .map(this::convertToDTO)
                .collect(toList());
    }


    private ContactDTO convertToDTO(Contact model) {
        ContactDTO dto = new ContactDTO();

        dto.setId(model.getId());
        dto.setDisplayName(model.getDisplayName());
        dto.setPhoneNumber(model.getPhoneNumber());
        dto.setUserId(model.getUserId());
        dto.setPhotoUri(model.getPhotoUri());
        dto.setPhoneNumberId(model.getPhoneNumberId());
        dto.setAddress(model.getAddress());
        dto.setBirthday(model.getBirthday());
        dto.setRelationship(model.getRelationship());
        dto.setNote(model.getNote());
        dto.setCreatedAt(model.getCreatedAt());
        dto.setUpdatedAt(model.getUpdatedAt());

        return dto;
    }
}
