package com.umass.hangout.controller;

import com.umass.hangout.exception.GroupNotFoundException;
import com.umass.hangout.exception.InvalidGroupIdFormatException;
import com.umass.hangout.service.ICSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ICSControllerTest {
    @Mock
    private ICSService icsService;

    @InjectMocks
    private ICSController icsController;

    @Test
    void generateICS_Success() throws IOException {
        // Arrange
        Long groupId = 1L;
        byte[] icsContent = "TEST ICS CONTENT".getBytes();
        when(icsService.generateICS(groupId)).thenReturn(icsContent);

        // Act
        ResponseEntity<byte[]> response = icsController.generateICS(String.valueOf(groupId));

        // Assert
        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertEquals(MediaType.parseMediaType("text/calendar"), response.getHeaders().getContentType()),
                () -> assertEquals(ContentDisposition.attachment().filename("event.ics").build(), response.getHeaders().getContentDisposition()),
                () -> assertArrayEquals(icsContent, response.getBody())
        );
    }

    @Test
    void generateICS_GroupNotFound() throws IOException {
        // Arrange
        Long groupId = 1L;
        when(icsService.generateICS(groupId)).thenThrow(new GroupNotFoundException("Group not found"));

        // Act
        ResponseEntity<byte[]> response = icsController.generateICS(String.valueOf(groupId));

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void generateICS_InvalidGroupIdFormat() {
        // Act & Assert
        assertThrows(InvalidGroupIdFormatException.class, () ->
                icsController.generateICS("invalid"));
    }

}
