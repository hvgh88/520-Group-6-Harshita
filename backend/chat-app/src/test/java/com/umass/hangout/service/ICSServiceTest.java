package com.umass.hangout.service;

import com.umass.hangout.entity.Group;
import com.umass.hangout.exception.GroupNotFoundException;
import com.umass.hangout.repository.jpa.GroupRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ICSServiceTest {
    @Mock
    private GroupRepository groupRepository;

    @InjectMocks
    private ICSService icsService;

    @Test
    void generateICS_Success() throws IOException {
        // Arrange
        Long groupId = 1L;
        Group group = new Group();
        group.setId(groupId);
        group.setName("Test Group");
        group.setLocation("Test Location");
        group.setDateTime(LocalDateTime.of(2024, 12, 21, 18, 30));

        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));

        // Act
        byte[] result = icsService.generateICS(groupId);

        // Assert
        String icsContent = new String(result);
        assertAll(
                () -> assertTrue(icsContent.contains("BEGIN:VCALENDAR")),
                () -> assertTrue(icsContent.contains("VERSION:2.0")),
                () -> assertTrue(icsContent.contains("BEGIN:VEVENT")),
                () -> assertTrue(icsContent.contains("SUMMARY:Test Group")),
                () -> assertTrue(icsContent.contains("LOCATION:Test Location")),
                () -> assertTrue(icsContent.contains("DTSTART:20241221T183000")),
                () -> assertTrue(icsContent.contains("END:VEVENT")),
                () -> assertTrue(icsContent.contains("END:VCALENDAR"))
        );
    }

    @Test
    void generateICS_GroupNotFound() {
        // Arrange
        Long groupId = 1L;
        when(groupRepository.findById(groupId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(GroupNotFoundException.class, () -> icsService.generateICS(groupId));
    }
}
