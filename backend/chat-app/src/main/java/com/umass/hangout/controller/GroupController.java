package com.umass.hangout.controller;

import com.umass.hangout.entity.Group;
import com.umass.hangout.entity.Message;
import com.umass.hangout.entity.MessageDTO;
import com.umass.hangout.exception.*;
import com.umass.hangout.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/group")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private static final Logger log = LoggerFactory.getLogger(GroupController.class);

    @PostMapping("/create")
    public ResponseEntity<String> createGroup(@Valid @RequestBody Group group, @RequestParam String userId) {
        try {
            Long userIdLong = Long.parseLong(userId);
            Group createdGroup = groupService.createGroup(group, userIdLong);
            return ResponseEntity.ok().body("Group created successfully with ID: " + createdGroup.getId());
        } catch (NumberFormatException e) {
            throw new InvalidInputException("Invalid user ID format: " + userId);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        } catch (InvalidDateTimeFormatException | InvalidGroupDataException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating group: " + e.getMessage());
        }
    }

    @PostMapping("/join")
    public ResponseEntity<String> joinGroup(@RequestParam String groupId, @RequestParam String userId) {
        try {
            Long groupIdLong = Long.parseLong(groupId);
            Long userIdLong = Long.parseLong(userId);
            groupService.joinGroup(groupIdLong, userIdLong);
            return ResponseEntity.ok().build();
        } catch (NumberFormatException e) {
            String errorMessage = groupId.matches("\\d+") ?
                    "Invalid user ID format: " + userId :
                    "Invalid group ID format: " + groupId;
            throw new InvalidInputException(errorMessage);
        } catch (GroupNotFoundException | UserNotFoundException | UserAlreadyInGroupException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/all")
    public List<Group> getAllGroups() {
        return groupService.getAllGroups();
    }

    @GetMapping("/user/{userId}")
    public List<Group> getUserGroups(@PathVariable Long userId) {
        return groupService.getUserGroups(userId);
    }


    @MessageMapping("/chat/{groupId}")
    public void handleMessage(@DestinationVariable Long groupId, MessageDTO messageDTO) {
//        log.info("Received message DTO: {}", messageDTO);

        if (messageDTO == null || messageDTO.getContent() == null || messageDTO.getSenderId() == null) {
//            log.error("Invalid message DTO received");
            return;
        }

        try {
            MessageDTO savedMessageDTO = groupService.sendMessage(groupId, messageDTO);
//            log.info("OUT OF SERVICE METHOD, BACK TO CONTROLLER");
//            log.info("Saved message DTO, preparing to broadcast: {}", savedMessageDTO);
            messagingTemplate.convertAndSend("/topic/messages/" + groupId, savedMessageDTO);
//            log.info("Message Broadcasted successfully");
        } catch (Exception e) {
            log.error("Error processing message", e);
        }
    }

    @GetMapping("/{groupId}/messages")
    public List<Message> getPreviousMessages(@PathVariable Long groupId) {
        return groupService.getGroupMessages(groupId);
    }
}