package critex.test.controller;

import critex.test.dto.request.CommentReplyRequest;
import critex.test.entity.Comment;
import critex.test.entity.CommentReply;
import critex.test.entity.Post;
import critex.test.entity.User;
import critex.test.repository.CommentReplyRepository;
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
public class CommentReplyControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CommentReplyRepository replyRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private Comment testComment;

    @BeforeEach
    void setUp() {
        replyRepository.deleteAll();
        commentRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();
        testUser = userRepository.save(User.builder().username("replier").build());
        Post post = postRepository.save(Post.builder().title("Post").userId(testUser.getId()).build());
        testComment = commentRepository.save(Comment.builder().content("Base Comment").postId(post.getId()).userId(testUser.getId()).build());
    }

    @Test
    void shouldCreateReply() throws Exception {
        CommentReplyRequest request = new CommentReplyRequest();
        request.setContent("Reply content");
        request.setCommentId(testComment.getId());
        request.setUserId(testUser.getId());

        mockMvc.perform(post("/comment-replies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Reply content"))
                .andExpect(jsonPath("$.commentId").value(testComment.getId()))
                .andExpect(jsonPath("$.userId").value(testUser.getId()));

        assertEquals(1, replyRepository.count());
    }

    @Test
    void shouldListRepliesWithFilter() throws Exception {
        replyRepository.save(CommentReply.builder().content("Reply 1").commentId(testComment.getId()).userId(testUser.getId()).build());
        replyRepository.save(CommentReply.builder().content("Reply 2").commentId(testComment.getId()).userId(testUser.getId()).build());
        replyRepository.save(CommentReply.builder().content("Other").commentId(testComment.getId()).userId(testUser.getId()).build());

        mockMvc.perform(get("/comment-replies")
                .param("content", "Reply"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].content", containsInAnyOrder("Reply 1", "Reply 2")));
    }
}
