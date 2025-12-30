package critex.test.service;

import critex.core.model.PageRequestParam;
import critex.core.model.ReportCondition;
import critex.core.repo.AbstractService;
import critex.test.dto.filter.PostFilter;
import critex.test.entity.Post;
import critex.test.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService extends AbstractService<Post, PostRepository> {

    public PostService(PostRepository repository) {
        super(repository);
    }

    public ReportCondition generateReport(PostFilter filter) {
        ReportCondition condition = new ReportCondition();
        condition.addContainsIgnoreCase("title", filter.getTitle());
        condition.addContainsIgnoreCase("content", filter.getContent());
        condition.addEqual("userId", filter.getUserId());
        condition.addJoinReport("user");
        return condition;
    }

    public Post findById(Long id) {
        return getEntityById(id, List.of("user"));
    }

    public Page<Post> getAll(ReportCondition condition, PageRequestParam pageRequest) {
        return findAllPage(condition, pageRequest);
    }

    public Post save(Post post) {
        return repository.save(post);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
