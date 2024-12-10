package com.umass.hangout.service;

import com.umass.hangout.controller.GroupController;
import com.umass.hangout.entity.Group;
import com.umass.hangout.entity.Message;
import com.umass.hangout.entity.MessageDTO;
import com.umass.hangout.entity.User;
import com.umass.hangout.exception.*;
import com.umass.hangout.repository.elasticsearch.GroupSearchRepository;
import com.umass.hangout.repository.elasticsearch.MessageSearchRepository;
import com.umass.hangout.repository.jpa.GroupRepository;
import com.umass.hangout.repository.jpa.MessageRepository;
import com.umass.hangout.repository.jpa.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.transaction.Transactional;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class GroupService {
    private static final Logger log = LoggerFactory.getLogger(GroupService.class);
    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupSearchRepository groupSearchRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private MessageSearchRepository messageSearchRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Group createGroup(Group group, Long userId) {
        // Validate user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        // Validate group data
        validateGroupData(group);

        Group savedGroup = groupRepository.save(group);
        groupSearchRepository.save(savedGroup);

        user.getGroupIds().add(savedGroup.getId());
        userRepository.save(user);

        return savedGroup;
    }

    private void validateGroupData(Group group) {
        if (group == null) {
            throw new InvalidGroupDataException("Group data cannot be null");
        }

        if (group.getName() == null || group.getName().trim().isEmpty()) {
            throw new InvalidGroupDataException("Group name cannot be empty");
        }

        if (group.getLocation() == null || group.getLocation().trim().isEmpty()) {
            throw new InvalidGroupDataException("Group location cannot be empty");
        }

        if (group.getDateTime() == null) {
            throw new InvalidGroupDataException("Group date/time cannot be null");
        }

        try {
            LocalDateTime dateTime = group.getDateTime();
            LocalDateTime now = LocalDateTime.now();

            if (dateTime.isBefore(now)) {
                throw new InvalidDateTimeFormatException("Group date/time must be in the future");
            }
        } catch (DateTimeException e) {
            throw new InvalidDateTimeFormatException("Invalid date-time format: " + e.getMessage());
        }
    }

    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }

    @Transactional
    public MessageDTO sendMessage(Long groupId, MessageDTO messageDTO) {
        log.debug("Entering sendMessage method. GroupId: {}, MessageDTO: {}", groupId, messageDTO);

        User sender = userRepository.findById(messageDTO.getSenderId())
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + messageDTO.getSenderId()));

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException("Group not found with ID: " + groupId));

        Message message = new Message();
        message.setContent(messageDTO.getContent());
        message.setSender(sender);
        message.setGroup(group);

        log.debug("Created new message: {}", message);

        Message savedMessage = messageRepository.save(message);
        messageSearchRepository.save(savedMessage);
        log.debug("Saved message: {}", savedMessage);

        return new MessageDTO(
                savedMessage.getId(),
                savedMessage.getContent(),
                savedMessage.getSender().getId(),
                savedMessage.getGroup().getId(),
                savedMessage.getSender().getUsername()
        );
    }

    @Transactional
    public void joinGroup(Long groupId, Long userId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException("Group not found with ID: " + groupId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        if (!user.getGroupIds().add(groupId)) {
            throw new UserAlreadyInGroupException("User " + userId + " is already a member of group " + groupId);
        }
        userRepository.save(user);
    }

    public List<Group> getUserGroups(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        return groupRepository.findAllById(user.getGroupIds());
    }

    public List<Message> getGroupMessages(Long groupId) {
        return messageRepository.findByGroupIdOrderByIdAsc(groupId);
    }
}