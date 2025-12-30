package critex.test.dto.filter;

import lombok.Data;

@Data
public class PostFilter {
    private String title;
    private String content;
    private Long userId;
}
