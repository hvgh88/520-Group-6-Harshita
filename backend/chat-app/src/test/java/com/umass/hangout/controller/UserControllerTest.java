//package com.umass.hangout.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.umass.hangout.entity.User;
//import com.umass.hangout.entity.Group;
//import com.umass.hangout.exception.UserNotFoundException;
//import com.umass.hangout.service.UserService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
//import java.util.Collections;
//import java.util.HashSet;
//
//import static org.mockito.ArgumentMatchers.anyLong;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//public class UserControllerTest {
//
//    @Mock
//    private UserService userService;
//
//    @InjectMocks
//    private UserController userController;
//
//    private MockMvc mockMvc;
//
//    private ObjectMapper objectMapper = new ObjectMapper();
//
//    private User user;
//
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
//
//        user = new User();
//        user.setId(1L);
//        user.setUsername("user1");
//        user.setGroupIds(new HashSet<>());
//    }
//
//    @Test
//    public void testCreateUser() throws Exception {
//        when(userService.createUser(any(User.class))).thenReturn(user);
//
//        mockMvc.perform(post("/user/create")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(user)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.username").value("user1"));
//    }
//
//    @Test
//    public void testGetUserGroups_UserNotFound() throws Exception {
//        when(userService.getUserGroups(anyLong())).thenThrow(new UserNotFoundException("User not found"));
//
//        mockMvc.perform(get("/user/groups/1"))
//                .andExpect(status().isNotFound())
//                .andExpect(content().string("User not found"));
//    }
//
//    @Test
//    public void testGetUserGroups_Success() throws Exception {
//        Group group = new Group();
//        group.setId(1L);
//        group.setName("Test Group");
//
//        when(userService.getUserGroups(anyLong())).thenReturn(Collections.singletonList(group));
//
//        mockMvc.perform(get("/user/groups/1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].name").value("Test Group"));
//    }
//
//    @Test
//    public void testGetUserGroupIds_UserNotFound() throws Exception {
//        when(userService.getUserGroupIds(anyLong())).thenThrow(new UserNotFoundException("User not found"));
//
//        mockMvc.perform(get("/user/group-ids/1"))
//                .andExpect(status().isNotFound())
//                .andExpect(content().string("User not found"));
//    }
//
//    @Test
//    public void testGetUserGroupIds_Success() throws Exception {
//        user.getGroupIds().add(1L);
//        when(userService.getUserGroupIds(anyLong())).thenReturn(user.getGroupIds());
//
//        mockMvc.perform(get("/user/group-ids/1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0]").value(1));
//    }
//}