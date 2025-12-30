package critex.test.service;

import critex.core.model.JoinReport;
import critex.core.model.PageRequestParam;
import critex.core.model.ReportCondition;
import critex.core.repo.AbstractService;
import critex.test.dto.filter.UserFilter;
import critex.test.entity.User;
import critex.test.repository.UserRepository;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class UserService extends AbstractService<User, UserRepository> {

    public UserService(UserRepository repository) {
        super(repository);
    }

    public ReportCondition generateReport(UserFilter filter) {
        ReportCondition condition = new ReportCondition();
        condition.addContainsIgnoreCase("username", filter.getUsername());
        condition.addContainsIgnoreCase("email", filter.getEmail());

//        join and filter post.title for user.posts but don't fetch and return them
        JoinReport postJoin = JoinReport.of("posts", JoinType.INNER, false);
        postJoin.getFilter().addLike("title", filter.getPostTitle());
        condition.addJoinReport(postJoin);
        return condition;
    }

    public User findById(Long id) {
        return getEntityById(id, null);
    }

    public Page<User> getAll(ReportCondition condition, PageRequestParam pageRequest) {
        return findAllPage(condition, pageRequest);
    }

    public User save(User user) {
        return repository.save(user);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
