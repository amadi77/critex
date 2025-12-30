package critex.test.dto.request;

import critex.test.entity.User;
import lombok.Data;

@Data
public class UserRequest {
    private String username;
    private String email;

    public User toEntity() {
        return User.builder()
                .username(username)
                .email(email)
                .build();
    }
}
