package critex.test.service;

import critex.core.model.PageRequestParam;
import critex.core.model.ReportCondition;
import critex.core.repo.AbstractService;
import critex.test.dto.filter.CommentFilter;
import critex.test.entity.Comment;
import critex.test.repository.CommentRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService extends AbstractService<Comment, CommentRepository> {

    public CommentService(CommentRepository repository) {
        super(repository);
    }

    public ReportCondition generateReport(CommentFilter filter) {
        ReportCondition condition = new ReportCondition();
        condition.addContainsIgnoreCase("content", filter.getContent());
        condition.addEqual("postId", filter.getPostId());
        condition.addEqual("userId", filter.getUserId());

        condition.addJoinReport("user");
        return condition;
    }

    public Comment findById(Long id) {
        return getEntityById(id, List.of("replies", "user"));
    }

    public Page<Comment> getAll(ReportCondition condition, PageRequestParam pageRequest) {
        return findAllPage(condition, pageRequest);
    }

    public Comment save(Comment comment) {
        return repository.save(comment);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
