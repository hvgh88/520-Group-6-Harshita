package com.umass.hangout.service;

import com.umass.hangout.entity.Group;
import com.umass.hangout.exception.GroupNotFoundException;
import com.umass.hangout.repository.jpa.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

@Service
public class ICSService {
    @Autowired
    private GroupRepository groupRepository;

    public byte[] generateICS(Long groupId) throws IOException {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException("Group not found with ID: " + groupId));

        String icsContent = String.format(
                "BEGIN:VCALENDAR\n" +
                        "VERSION:2.0\n" +
                        "BEGIN:VEVENT\n" +
                        "SUMMARY:%s\n" +
                        "LOCATION:%s\n" +
                        "DTSTART:%s\n" +
                        "END:VEVENT\n" +
                        "END:VCALENDAR",
                group.getName(),
                group.getLocation(),
                group.getDateTime().format(DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss"))
        );

        return icsContent.getBytes();
    }
}