package com.umass.hangout.service;

import com.umass.hangout.entity.User;
import com.umass.hangout.entity.Group;
import com.umass.hangout.exception.UserNotFoundException;
import com.umass.hangout.repository.jpa.UserRepository;
import com.umass.hangout.repository.jpa.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public Set<Long> getUserGroupIds(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        return user.getGroupIds();
    }

    public List<Group> getUserGroups(Long userId) {
        Set<Long> groupIds = getUserGroupIds(userId);
        return groupRepository.findAllById(groupIds);
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
    }
}