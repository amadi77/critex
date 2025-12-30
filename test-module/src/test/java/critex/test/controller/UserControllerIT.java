package critex.test.controller;

import critex.test.dto.request.UserRequest;
import critex.test.dto.response.UserResponse;
import critex.test.entity.User;
import critex.test.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void shouldCreateUser() throws Exception {
        UserRequest request = new UserRequest();
        request.setUsername("johndoe");
        request.setEmail("john@example.com");

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("johndoe"))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.id").exists());

        assertEquals(1, userRepository.count());
    }

    @Test
    void shouldGetByUserId() throws Exception {
        User user = User.builder()
                .username("janedoe")
                .email("jane@example.com")
                .build();
        user = userRepository.save(user);

        mockMvc.perform(get("/users/" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("janedoe"))
                .andExpect(jsonPath("$.email").value("jane@example.com"));
    }

    @Test
    void shouldListUsersWithFilter() throws Exception {
        userRepository.save(User.builder().username("user1").email("user1@test.com").build());
        userRepository.save(User.builder().username("user2").email("user2@test.com").build());
        userRepository.save(User.builder().username("other").email("other@test.com").build());

        mockMvc.perform(get("/users")
                .param("username", "user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].username", containsInAnyOrder("user1", "user2")));
    }

    @Test
    void shouldDeleteUser() throws Exception {
        User user = userRepository.save(User.builder().username("deleteMe").build());

        mockMvc.perform(delete("/users/" + user.getId()))
                .andExpect(status().isOk());

        assertEquals(0, userRepository.count());
    }
}
