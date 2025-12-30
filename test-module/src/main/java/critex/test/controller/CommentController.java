package critex.test.controller;

import critex.core.model.PageRequestParam;
import critex.test.dto.filter.CommentFilter;
import critex.test.dto.request.CommentRequest;
import critex.test.dto.response.CommentResponse;
import critex.test.entity.Comment;
import critex.test.entity.Post;
import critex.test.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public CommentResponse create(@RequestBody CommentRequest request) {
        Comment comment = request.toEntity();
        comment = commentService.save(comment);
        return new CommentResponse(comment);
    }

    @GetMapping("/{id}")
    public CommentResponse getById(@PathVariable Long id) {
        Comment comment = commentService.findById(id);
        return new CommentResponse(comment);
    }

    @GetMapping
    public List<CommentResponse> list(@ParameterObject CommentFilter filter, @ParameterObject PageRequestParam pageRequest) {
        Page<Comment> page = commentService.getAll(commentService.generateReport(filter), pageRequest);
        return page.map(CommentResponse::new).getContent();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        commentService.deleteById(id);
    }
}
