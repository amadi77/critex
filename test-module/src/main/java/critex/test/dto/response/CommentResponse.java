package critex.test.dto.response;

import critex.test.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {
    private Long id;
    private String content;
    private Long postId;
    private Long userId;
    private UserResponse user;
    private List<CommentReplyResponse> replies;

    public CommentResponse(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.postId = comment.getPostId();
        this.userId = comment.getUserId();
        if (comment.getUser() != null && Hibernate.isInitialized(comment.getUser())) {
            this.user = new UserResponse(comment.getUser());
        }
        if (comment.getReplies() != null && Hibernate.isInitialized(comment.getReplies())) {
            this.replies = comment.getReplies().stream().map(CommentReplyResponse::new).toList();
        }
    }
}
