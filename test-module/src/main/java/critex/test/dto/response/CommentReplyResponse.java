package critex.test.dto.response;

import critex.test.entity.CommentReply;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentReplyResponse {
    private Long id;
    private String content;
    private Long commentId;
    private Long userId;

    public CommentReplyResponse(CommentReply reply) {
        this.id = reply.getId();
        this.content = reply.getContent();
        this.commentId = reply.getCommentId();
        this.userId = reply.getUserId();
    }
}
