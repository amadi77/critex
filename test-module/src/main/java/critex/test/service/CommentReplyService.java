package critex.test.service;

import critex.core.model.PageRequestParam;
import critex.core.model.ReportCondition;
import critex.core.repo.AbstractService;
import critex.test.dto.filter.CommentReplyFilter;
import critex.test.entity.CommentReply;
import critex.test.repository.CommentReplyRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentReplyService extends AbstractService<CommentReply, CommentReplyRepository> {

    public CommentReplyService(CommentReplyRepository repository) {
        super(repository);
    }

    public ReportCondition generateReport(CommentReplyFilter filter) {
        ReportCondition condition = new ReportCondition();
        condition.addContainsIgnoreCase("content", filter.getContent());
        condition.addEqual("commentId", filter.getCommentId());
        condition.addEqual("userId", filter.getUserId());

        condition.addJoinReport("user");
        return condition;
    }

    public CommentReply findById(Long id) {
        return getEntityById(id, List.of("user"));
    }

    public Page<CommentReply> getAll(ReportCondition condition, PageRequestParam pageRequest) {
        return findAllPage(condition, pageRequest);
    }

    public CommentReply save(CommentReply commentReply) {
        return repository.save(commentReply);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
