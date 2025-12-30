package critex.test.controller;

import critex.test.dto.request.CommentRequest;
import critex.test.entity.Comment;
import critex.test.entity.Post;
import critex.test.entity.User;
import critex.test.repository.CommentRepository;
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
public class CommentControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private Post testPost;

    @BeforeEach
    void setUp() {
        commentRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();
        testUser = userRepository.save(User.builder().username("commenter").build());
        testPost = postRepository.save(Post.builder().title("Target Post").userId(testUser.getId()).build());
    }

    @Test
    void shouldCreateComment() throws Exception {
        CommentRequest request = new CommentRequest();
        request.setContent("Nice post!");
        request.setPostId(testPost.getId());
        request.setUserId(testUser.getId());

        mockMvc.perform(post("/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Nice post!"))
                .andExpect(jsonPath("$.postId").value(testPost.getId()))
                .andExpect(jsonPath("$.userId").value(testUser.getId()));

        assertEquals(1, commentRepository.count());
    }

    @Test
    void shouldListCommentsWithFilter() throws Exception {
        commentRepository.save(Comment.builder().content("Comment 1").postId(testPost.getId()).userId(testUser.getId()).build());
        commentRepository.save(Comment.builder().content("Comment 2").postId(testPost.getId()).userId(testUser.getId()).build());
        commentRepository.save(Comment.builder().content("Other").postId(testPost.getId()).userId(testUser.getId()).build());

        mockMvc.perform(get("/comments")
                .param("content", "Comment"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].content", containsInAnyOrder("Comment 1", "Comment 2")));
    }
}
