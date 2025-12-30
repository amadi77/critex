package critex.test.dto.request;

import critex.test.entity.Comment;
import critex.test.entity.Post;
import critex.test.entity.User;
import lombok.Data;

@Data
public class CommentRequest {
    private String content;
    private Long postId;
    private Long userId;

    public Comment toEntity() {
        return Comment.builder()
                .content(content)
                .postId(postId)
                .userId(userId)
                .build();
    }
}
