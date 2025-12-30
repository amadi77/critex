package critex.test.dto.filter;

import lombok.Data;

@Data
public class CommentFilter {
    private String content;
    private Long postId;
    private Long userId;
}
