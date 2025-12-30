package critex.test.controller;

import critex.test.dto.request.PostRequest;
import critex.test.entity.Post;
import critex.test.entity.User;
import critex.test.repository.PostRepository;
import critex.test.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class PostControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;

    @BeforeEach
    void setUp() {
        postRepository.deleteAll();
        userRepository.deleteAll();
        testUser = userRepository.save(User.builder().username("author").email("author@test.com").build());
    }

    @Test
    void shouldCreatePost() throws Exception {
        PostRequest request = new PostRequest();
        request.setTitle("Hello World");
        request.setContent("This is my first post");
        request.setUserId(testUser.getId());

        mockMvc.perform(post("/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Hello World"))
                .andExpect(jsonPath("$.userId").value(testUser.getId()));

        assertEquals(1, postRepository.count());
    }

    @Test
    void shouldGetPostById() throws Exception {
        Post post = Post.builder()
                .title("Fetch Me")
                .content("Content")
                .userId(testUser.getId())
                .build();
        post = postRepository.save(post);

        mockMvc.perform(get("/posts/" + post.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Fetch Me"))
                .andExpect(jsonPath("$.userId").value(testUser.getId()));
    }

    @Test
    void shouldListPostsWithFilter() throws Exception {
        postRepository.save(Post.builder().title("Post 1").userId(testUser.getId()).build());
        postRepository.save(Post.builder().title("Post 2").userId(testUser.getId()).build());
        postRepository.save(Post.builder().title("Other").userId(testUser.getId()).build());

        mockMvc.perform(get("/posts")
                .param("title", "Post"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].title", containsInAnyOrder("Post 1", "Post 2")));
    }
}
