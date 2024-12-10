package com.umass.hangout.service;

import com.umass.hangout.entity.Group;
import com.umass.hangout.entity.User;
import com.umass.hangout.exception.*;
import com.umass.hangout.repository.elasticsearch.GroupSearchRepository;
import com.umass.hangout.repository.jpa.GroupRepository;
import com.umass.hangout.repository.jpa.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GroupServiceTest {
    @Mock
    private GroupRepository groupRepository;

    @Mock
    private GroupSearchRepository groupSearchRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GroupService groupService;

    private User testUser;
    private Group testGroup;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("Test User");

        testGroup = new Group();
        testGroup.setId(1L);
        testGroup.setName("Test Group");
        testGroup.setLocation("Test Location");
        testGroup.setDateTime(LocalDateTime.now().plusDays(1));
    }

    @Test
    void joinGroup_Success() {
        // Arrange
        when(groupRepository.findById(1L)).thenReturn(Optional.of(testGroup));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Act
        groupService.joinGroup(1L, 1L);

        // Assert
        verify(userRepository).save(testUser);
    }

    @Test
    void joinGroup_GroupNotFound() {
        // Arrange
        when(groupRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(GroupNotFoundException.class, () ->
                groupService.joinGroup(1L, 1L));
    }

    @Test
    void joinGroup_UserNotFound() {
        // Arrange
        when(groupRepository.findById(1L)).thenReturn(Optional.of(testGroup));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () ->
                groupService.joinGroup(1L, 1L));
    }

    @Test
    void joinGroup_UserAlreadyInGroup() {
        // Arrange
        testUser.getGroupIds().add(1L);
        when(groupRepository.findById(1L)).thenReturn(Optional.of(testGroup));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Act & Assert
        assertThrows(UserAlreadyInGroupException.class, () ->
                groupService.joinGroup(1L, 1L));
    }

    @Test
    void createGroup_Success() {
        // Arrange
        Group group = new Group();
        group.setId(1L);
        group.setName("Test Group");
        group.setLocation("Test Location");
        group.setDateTime(LocalDateTime.now().plusDays(1));

        User user = new User();
        user.setId(1L);
        user.setUsername("Test User");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(groupRepository.save(any(Group.class))).thenReturn(group);
        when(groupSearchRepository.save(any(Group.class))).thenReturn(group);

        // Act
        Group result = groupService.createGroup(group, 1L);

        // Assert
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(group.getId(), result.getId()),
                () -> assertEquals(group.getName(), result.getName()),
                () -> verify(groupRepository).save(any(Group.class)),
                () -> verify(groupSearchRepository).save(any(Group.class)),
                () -> verify(userRepository).save(user)
        );
    }

    @Test
    void createGroup_UserNotFound() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () ->
                groupService.createGroup(testGroup, 1L));
    }

    @Test
    void createGroup_NullGroup() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Act & Assert
        assertThrows(InvalidGroupDataException.class, () ->
                groupService.createGroup(null, 1L));
    }

    @Test
    void createGroup_EmptyGroupName() {
        // Arrange
        testGroup.setName("");
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Act & Assert
        assertThrows(InvalidGroupDataException.class, () ->
                groupService.createGroup(testGroup, 1L));
    }

    @Test
    void createGroup_EmptyLocation() {
        // Arrange
        testGroup.setLocation("");
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Act & Assert
        assertThrows(InvalidGroupDataException.class, () ->
                groupService.createGroup(testGroup, 1L));
    }

    @Test
    void createGroup_PastDateTime() {
        // Arrange
        testGroup.setDateTime(LocalDateTime.now().minusDays(1));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Act & Assert
        assertThrows(InvalidDateTimeFormatException.class, () ->
                groupService.createGroup(testGroup, 1L));
    }

}
