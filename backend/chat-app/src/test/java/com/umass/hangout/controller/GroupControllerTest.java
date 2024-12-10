package com.umass.hangout.controller;

import com.umass.hangout.entity.Group;
import com.umass.hangout.exception.*;
import com.umass.hangout.service.GroupService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupControllerTest {
    @Mock
    private GroupService groupService;

    @InjectMocks
    private GroupController groupController;

    private Group testGroup;

    @BeforeEach
    void setUp() {
        testGroup = new Group();
        testGroup.setId(1L);
        testGroup.setName("Test Group");
        testGroup.setLocation("Test Location");
        testGroup.setDateTime(LocalDateTime.now().plusDays(1));
    }

    @Test
    void joinGroup_Success() {
        // Arrange
        doNothing().when(groupService).joinGroup(1L, 1L);

        // Act
        ResponseEntity<String> response = groupController.joinGroup("1", "1");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void joinGroup_InvalidGroupId() {
        // Act & Assert
        assertThrows(InvalidInputException.class, () ->
                groupController.joinGroup("invalid", "1"));
    }

    @Test
    void joinGroup_InvalidUserId() {
        // Act & Assert
        assertThrows(InvalidInputException.class, () ->
                groupController.joinGroup("1", "invalid"));
    }

    @Test
    void joinGroup_GroupNotFound() {
        // Arrange
        doThrow(new GroupNotFoundException("Group not found"))
                .when(groupService).joinGroup(1L, 1L);

        // Act
        ResponseEntity<String> response = groupController.joinGroup("1", "1");

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Group not found", response.getBody());
    }

    @Test
    void joinGroup_UserNotFound() {
        // Arrange
        doThrow(new UserNotFoundException("User not found"))
                .when(groupService).joinGroup(1L, 1L);

        // Act
        ResponseEntity<String> response = groupController.joinGroup("1", "1");

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("User not found", response.getBody());
    }

    @Test
    void joinGroup_UserAlreadyInGroup() {
        // Arrange
        doThrow(new UserAlreadyInGroupException("User already in group"))
                .when(groupService).joinGroup(1L, 1L);

        // Act
        ResponseEntity<String> response = groupController.joinGroup("1", "1");

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("User already in group", response.getBody());
    }

    @Test
    void createGroup_Success() {
        // Arrange
        when(groupService.createGroup(any(Group.class), eq(1L))).thenReturn(testGroup);

        // Act
        ResponseEntity<?> response = groupController.createGroup(testGroup, "1");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void createGroup_InvalidUserId() {
        // Act & Assert
        assertThrows(InvalidInputException.class, () ->
                groupController.createGroup(testGroup, "invalid"));
    }

    @Test
    void createGroup_UserNotFound() {
        // Arrange
        when(groupService.createGroup(any(Group.class), eq(1L)))
                .thenThrow(new UserNotFoundException("User not found"));

        // Act
        ResponseEntity<?> response = groupController.createGroup(testGroup, "1");

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found", response.getBody());
    }

    @Test
    void createGroup_InvalidGroupData() {
        // Arrange
        when(groupService.createGroup(any(Group.class), eq(1L)))
                .thenThrow(new InvalidGroupDataException("Invalid group data"));

        // Act
        ResponseEntity<?> response = groupController.createGroup(testGroup, "1");

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid group data", response.getBody());
    }

    @Test
    void createGroup_InvalidDateTime() {
        // Arrange
        when(groupService.createGroup(any(Group.class), eq(1L)))
                .thenThrow(new InvalidDateTimeFormatException("Invalid date/time"));

        // Act
        ResponseEntity<?> response = groupController.createGroup(testGroup, "1");

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid date/time", response.getBody());
    }
}