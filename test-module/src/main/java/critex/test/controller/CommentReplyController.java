package critex.test.controller;

import critex.core.model.PageRequestParam;
import critex.test.dto.filter.CommentReplyFilter;
import critex.test.dto.request.CommentReplyRequest;
import critex.test.dto.response.CommentReplyResponse;
import critex.test.entity.Comment;
import critex.test.entity.CommentReply;
import critex.test.service.CommentReplyService;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comment-replies")
@RequiredArgsConstructor
public class CommentReplyController {

    private final CommentReplyService commentReplyService;

    @PostMapping
    public CommentReplyResponse create(@RequestBody CommentReplyRequest request) {
        CommentReply commentReply = request.toEntity();
        commentReply = commentReplyService.save(commentReply);
        return new CommentReplyResponse(commentReply);
    }

    @GetMapping("/{id}")
    public CommentReplyResponse getById(@PathVariable Long id) {
        CommentReply commentReply = commentReplyService.findById(id);
        return new CommentReplyResponse(commentReply);
    }

    @GetMapping
    public List<CommentReplyResponse> list(@ParameterObject CommentReplyFilter filter, @ParameterObject PageRequestParam pageRequest) {
        Page<CommentReply> page = commentReplyService.getAll(commentReplyService.generateReport(filter), pageRequest);
        return page.map(CommentReplyResponse::new).getContent();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        commentReplyService.deleteById(id);
    }
}
