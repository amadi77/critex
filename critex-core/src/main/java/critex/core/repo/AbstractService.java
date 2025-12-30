package critex.core.repo;

import critex.core.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.*;

/**
 * Abstract service class that provides basic CRUD operations without generateReport methods.
 * This class extends Parameterized to support automatic type extraction.
 *
 * @author Ahmad Reza Mokhtari
 */
public abstract class AbstractService<T, R extends JpaSpecificationExecutor<T>> extends SpecificationGenerator<T> {

    protected R repository;

    protected AbstractService(R repository) {
        this.repository = repository;
    }

    /**
     * Get the repository instance
     */
    protected R getRepository() {
        return repository;
    }

    // ============= FILTERABLE METHODS (getAll APIs with generateReport) =============

    /**
     * Find all entities with filter and pagination
     */
    protected List<T> findAll(ReportCondition condition, PageRequestParam pageRequest) {
        return getRepository().findAll(toPredicate(condition), PaginationUtils.getPageRequest(pageRequest)).toList();
    }

    /**
     * Find all dto with filter
     */
    protected <DTO> List<DTO> findAllDto(ReportCondition condition, Class<DTO> clazz) {
        return getRepository().findBy(toPredicate(condition), q -> q.as(clazz).all());
    }

    /**
     * Find all ids with filter
     */
    protected List<Object> findAllId(ReportCondition condition) {
        condition.setDeActiveFetch(true);
        return getRepository().findBy(toPredicate(condition), q -> q.as(IdDto.class).all()).stream().map(IdDto::id).toList();
    }

    /**
     * Find all entities with filter, pagination and joins
     */
    protected List<T> findAll(ReportCondition condition, PageRequestParam pageRequest, Collection<String> joins) {
        if (joins != null) {
            joins.forEach(condition::addJoinReport);
        }
        return getRepository()
                .findAll(toPredicate(condition), PaginationUtils.getPageRequest(pageRequest))
                .toList();
    }

    /**
     * Find all entities as page with filter, pagination and joins
     */
    protected Page<T> findAllPage(ReportCondition condition, PageRequestParam pageRequest, Collection<String> joins) {
        if (joins != null) {
            joins.forEach(condition::addJoinReport);
        }
        return getRepository().findAll(toPredicate(condition), PaginationUtils.getPageRequest(pageRequest));
    }

    /**
     * Find all entities as page with condition and pagination
     */
    protected Page<T> findAllPage(ReportCondition condition, PageRequestParam pageRequest) {
        return getRepository().findAll(toPredicate(condition), PaginationUtils.getPageRequest(pageRequest));
    }

    // ============= AGGREGATION METHODS WITH FILTER =============

    /**
     * Count entities with filter
     */
    protected long count(ReportCondition condition) {
        return getRepository().count(toPredicate(condition));
    }

    // ============= SINGLE RESULT METHODS WITH FILTER =============

    protected Optional<T> findFirst(ReportCondition condition) {
        PageRequestParam pageRequest = PageRequestParam.builder()
                .pageNumber(0)
                .pageSize(1)
                .build();
        List<T> results = findAll(condition, pageRequest);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.getFirst());
    }

    // ============= SEARCH METHODS =============

    /**
     * Search entities with text in multiple fields
     */
    protected List<T> search(String searchText, List<String> searchFields, PageRequestParam pageRequest) {
        ReportCondition condition = new ReportCondition();
        ReportFilter orFilter = new ReportFilter();

        for (String field : searchFields) {
            orFilter.addContainsIgnoreCase(field, searchText);
        }

        condition.setOrFilter(orFilter);
        return findAll(condition, pageRequest);
    }

    /**
     * Search entities with text in multiple fields and return as page
     */
    protected Page<T> searchPage(String searchText, List<String> searchFields, PageRequestParam pageRequest) {
        ReportCondition condition = new ReportCondition();
        ReportFilter orFilter = new ReportFilter();

        for (String field : searchFields) {
            orFilter.addContainsIgnoreCase(field, searchText);
        }

        condition.setOrFilter(orFilter);
        return findAllPage(condition, pageRequest);
    }

    /**
     * Find entity by ID with joins
     */
    protected T getEntityById(Object id, Collection<String> joins) {
        if (id == null) {
            return null;
        }
        List<JoinReport> joinReports = joins != null ? joins.stream().map(JoinReport::of).toList() : new ArrayList<>();
        List<T> result = repository.findAll(getById(id, joinReports));
        return result.isEmpty() ? null : result.getFirst();
    }

    /**
     * Find entity by ID with inner joins
     */
    protected T getEntityByIdInnerJoin(Object id, Collection<JoinReport> joins) {
        if (id == null) {
            return null;
        }
        List<T> result = repository.findAll(getById(id, joins));
        return result.isEmpty() ? null : result.getFirst();
    }

    /**
     * Find optional entity by ID with joins
     */
    protected Optional<T> getEntityOptionalById(Object id, Collection<String> joins) {
        T entity = getEntityById(id, joins);
        return Optional.ofNullable(entity);
    }

    /**
     * Find entities by equal conditions
     */
    protected List<T> getListByEqualConditions(Map<String, Object> conditions, Collection<String> joins) {
        List<JoinReport> joinReports = joins != null ? joins.stream().map(JoinReport::of).toList() : new ArrayList<>();
        ReportCondition reportCondition = new ReportCondition();
        conditions.forEach(reportCondition::addEqual);
        joinReports.forEach(reportCondition::addJoinReport);
        return repository.findAll(toPredicate(reportCondition));
    }

    /**
     * Find entities by equal conditions with inner joins
     */
    protected List<T> getListByEqualConditionsInnerJoin(Map<String, Object> conditions, Collection<JoinReport> joins) {
        ReportCondition reportCondition = new ReportCondition();
        conditions.forEach(reportCondition::addEqual);
        if (joins != null) {
            joins.forEach(reportCondition::addJoinReport);
        }
        return repository.findAll(toPredicate(reportCondition));
    }

    /**
     * Find entities by conditions with inner joins
     */
    protected List<T> getListByConditionsInnerJoin(ReportCondition mainCondition, Collection<JoinReport> joins) {
        if (joins != null) {
            joins.forEach(mainCondition::addJoinReport);
        }
        return repository.findAll(toPredicate(mainCondition));
    }

    /**
     * Find single entity by equal conditions with inner joins
     */
    protected T getByEqualConditionsInnerJoin(Map<String, Object> conditions, Collection<JoinReport> joins) {
        List<T> result = getListByEqualConditionsInnerJoin(conditions, joins);
        return result.isEmpty() ? null : result.get(0);
    }

    /**
     * Find single entity by conditions with inner joins
     */
    protected T getByConditionsInnerJoin(ReportCondition mainCondition, Collection<JoinReport> joins) {
        List<T> result = getListByConditionsInnerJoin(mainCondition, joins);
        return result.isEmpty() ? null : result.get(0);
    }


    /**
     * Check if entity exists by condition
     */
    protected boolean exists(ReportCondition condition) {
        return repository.count(toPredicate(condition)) > 0;
    }

    /**
     * Check if entity exists by equal conditions
     */
    protected boolean exists(Map<String, Object> equalConditions) {
        ReportCondition condition = new ReportCondition();
        equalConditions.forEach(condition::addEqual);
        return exists(condition);
    }

    /**
     * Find entities by IDs
     */
    protected List<T> findAllByIds(List<Object> ids) {
        return repository.findAll(getAllByIds(ids, new ArrayList<>()));
    }

    /**
     * Find entities by IDs with joins
     */
    protected List<T> findAllByIds(List<Object> ids, Collection<String> joins) {
        List<JoinReport> joinReports = joins != null ? joins.stream().map(JoinReport::of).toList() : new ArrayList<>();
        return repository.findAll(getAllByIds(ids, joinReports));
    }

    /**
     * Create new condition
     */
    protected ReportCondition newCondition() {
        return new ReportCondition();
    }

    /**
     * Create new filter
     */
    protected ReportFilter newFilter() {
        return new ReportFilter();
    }

    /**
     * Create join report
     */
    protected JoinReport join(String path) {
        return JoinReport.of(path);
    }

    /**
     * Create multiple join reports
     */
    protected List<JoinReport> joins(String... paths) {
        List<JoinReport> joinReports = new ArrayList<>();
        for (String path : paths) {
            joinReports.add(join(path));
        }
        return joinReports;
    }

    protected record IdDto(Object id){}
}
