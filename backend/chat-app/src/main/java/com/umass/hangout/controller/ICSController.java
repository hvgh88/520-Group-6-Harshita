package com.umass.hangout.controller;

import com.umass.hangout.exception.GroupNotFoundException;
import com.umass.hangout.exception.InvalidGroupIdFormatException;
import com.umass.hangout.service.ICSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ICSController {
    @Autowired
    private ICSService icsService;

    @GetMapping("/group/{groupId}/ics")
    public ResponseEntity<byte[]> generateICS(@PathVariable String groupId) {
        try {
            Long groupIdLong = Long.parseLong(groupId);
            byte[] icsContent = icsService.generateICS(groupIdLong);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("text/calendar"));
            headers.setContentDisposition(ContentDisposition.attachment().filename("event.ics").build());

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(icsContent);
        } catch (NumberFormatException e) {
            throw new InvalidGroupIdFormatException("Invalid group ID format: " + groupId);
        } catch (GroupNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage().getBytes());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
