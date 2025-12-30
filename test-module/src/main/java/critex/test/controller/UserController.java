package critex.test.controller;

import critex.core.model.PageRequestParam;
import critex.test.dto.filter.UserFilter;
import critex.test.dto.request.UserRequest;
import critex.test.dto.response.UserResponse;
import critex.test.entity.User;
import critex.test.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserResponse create(@RequestBody UserRequest request) {
        User user = request.toEntity();
        user = userService.save(user);
        return new UserResponse(user);
    }

    @GetMapping("/{id}")
    public UserResponse getById(@PathVariable Long id) {
        User user = userService.findById(id);
        return new UserResponse(user);
    }

    @GetMapping
    public List<UserResponse> list(@ParameterObject UserFilter filter, @ParameterObject PageRequestParam pageRequest) {
        Page<User> page = userService.getAll(userService.generateReport(filter), pageRequest);
        return page.map(UserResponse::new).getContent();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        userService.deleteById(id);
    }
}
