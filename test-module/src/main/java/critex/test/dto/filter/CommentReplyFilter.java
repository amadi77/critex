package critex.test.dto.filter;

import lombok.Data;

@Data
public class CommentReplyFilter {
    private String content;
    private Long commentId;
    private Long userId;
}
