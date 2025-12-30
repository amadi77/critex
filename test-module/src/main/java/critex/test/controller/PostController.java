package critex.test.controller;

import critex.core.model.PageRequestParam;
import critex.test.dto.filter.PostFilter;
import critex.test.dto.request.PostRequest;
import critex.test.dto.response.PostResponse;
import critex.test.entity.Post;
import critex.test.entity.User;
import critex.test.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public PostResponse create(@RequestBody PostRequest request) {
        Post post = request.toEntity();
        post = postService.save(post);
        return new PostResponse(post);
    }

    @GetMapping("/{id}")
    public PostResponse getById(@PathVariable Long id) {
        Post post = postService.findById(id);
        return new PostResponse(post);
    }

    @GetMapping
    public List<PostResponse> list(@ParameterObject PostFilter filter, @ParameterObject PageRequestParam pageRequest) {
        Page<Post> page = postService.getAll(postService.generateReport(filter), pageRequest);
        return page.map(PostResponse::new).getContent();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        postService.deleteById(id);
    }
}
