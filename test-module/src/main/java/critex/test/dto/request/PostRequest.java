package critex.test.dto.request;

import critex.test.entity.Post;
import critex.test.entity.User;
import lombok.Data;

@Data
public class PostRequest {
    private String title;
    private String content;
    private Long userId;

    public Post toEntity() {
        return Post.builder()
                .title(title)
                .content(content)
                .userId(userId)
                .build();
    }
}
