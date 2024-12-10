//package com.umass.hangout.service;
//
//import com.umass.hangout.entity.User;
//import com.umass.hangout.entity.Group;
//import com.umass.hangout.exception.UserNotFoundException;
//import com.umass.hangout.repository.jpa.UserRepository;
//import com.umass.hangout.repository.jpa.GroupRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import java.util.HashSet;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//public class UserServiceTest {
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private GroupRepository groupRepository;
//
//    @InjectMocks
//    private UserService userService;
//
//    private User user;
//
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//        user = new User();
//        user.setId(1L);
//        user.setUsername("user1");
//        user.setGroupIds(new HashSet<>());
//    }
//
//    @Test
//    public void testCreateUser() {
//        when(userRepository.save(any(User.class))).thenReturn(user);
//        User createdUser = userService.createUser(user);
//        assertNotNull(createdUser);
//        assertEquals("user1", createdUser.getUsername());
//    }
//
//    @Test
//    public void testGetUserGroupIds_UserNotFound() {
//        when(userRepository.findById(1L)).thenReturn(Optional.empty());
//        assertThrows(UserNotFoundException.class, () -> userService.getUserGroupIds(1L));
//    }
//
//    @Test
//    public void testGetUserGroups_UserNotFound() {
//        when(userRepository.findById(1L)).thenReturn(Optional.empty());
//        assertThrows(UserNotFoundException.class, () -> userService.getUserGroups(1L));
//    }
//}