package critex.test.dto.request;

import critex.test.entity.CommentReply;
import lombok.Data;

@Data
public class CommentReplyRequest {
    private String content;
    private Long commentId;
    private Long userId;

    public CommentReply toEntity() {
        return CommentReply.builder()
                .content(content)
                .commentId(commentId)
                .userId(userId)
                .build();
    }
}
