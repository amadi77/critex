package critex.core.repo;

import critex.core.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.*;

/**
 * Enhanced Base Service with additional utility methods for easier usage
 * This is an improved version of BaseService with more features and better API
 */
public interface EnhancedBaseService<T> extends FilterableSpecificationGenerator<T> {

    JpaSpecificationExecutor<T> getRepository();

    // ============= BASIC CRUD OPERATIONS =============
    
    /**
     * Find entity by ID with optional joins
     */
    default T findById(Object id, Collection<String> joins) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        List<JoinReport> joinReports = joins != null ? joins.stream().map(JoinReport::of).toList() : new ArrayList<>();
        List<T> result = getRepository().findAll(getById(id, joinReports));
        if (!result.isEmpty()) {
            return result.getFirst();
        }
        throw new RuntimeException("Entity not found with ID: " + id);
    }
    
    default T findById(Object id) {
        return findById(id, new ArrayList<>());
    }
    
    /**
     * Find entity by ID with JoinReport objects
     */
    default T findByIdWithJoins(Object id, Collection<JoinReport> joins) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        List<T> result = getRepository().findAll(getById(id, joins));
        if (!result.isEmpty()) {
            return result.getFirst();
        }
        throw new RuntimeException("Entity not found with ID: " + id);
    }
    
    /**
     * Find entity by ID optionally
     */
    default Optional<T> findByIdOptional(Object id, Collection<String> joins) {
        if (id == null) {
            return Optional.empty();
        }
        List<JoinReport> joinReports = joins != null ? joins.stream().map(JoinReport::of).toList() : new ArrayList<>();
        List<T> result = getRepository().findAll(getById(id, joinReports));
        return result.isEmpty() ? Optional.empty() : Optional.of(result.getFirst());
    }
    
    default Optional<T> findByIdOptional(Object id) {
        return findByIdOptional(id, new ArrayList<>());
    }

    // ============= QUERY BY CONDITIONS =============
    
    /**
     * Find entities by equal conditions
     */
    default List<T> findByEqualConditions(Map<String, Object> conditions, Collection<String> joins) {
        List<JoinReport> joinReports = joins != null ? joins.stream().map(JoinReport::of).toList() : new ArrayList<>();
        ReportCondition reportCondition = new ReportCondition();
        conditions.forEach(reportCondition::addEqual);
        joinReports.forEach(reportCondition::addJoinReport);
        return getRepository().findAll(toPredicate(reportCondition));
    }
    
    default List<T> findByEqualConditions(Map<String, Object> conditions) {
        return findByEqualConditions(conditions, new ArrayList<>());
    }
    
    /**
     * Find entities by conditions with joins
     */
    default List<T> findByConditions(ReportCondition condition, Collection<JoinReport> joins) {
        if (joins != null) {
            joins.forEach(condition::addJoinReport);
        }
        return getRepository().findAll(toPredicate(condition));
    }
    
    default List<T> findByConditions(ReportCondition condition) {
        return findByConditions(condition, new ArrayList<>());
    }
    
    /**
     * Find single entity by conditions
     */
    default T findOneByConditions(ReportCondition condition, Collection<JoinReport> joins) {
        List<T> result = findByConditions(condition, joins);
        if (result.isEmpty()) {
            throw new RuntimeException("Entity not found");
        }
        return result.getFirst();
    }
    
    default T findOneByConditions(ReportCondition condition) {
        return findOneByConditions(condition, new ArrayList<>());
    }

    // ============= PAGINATION METHODS =============
    
    default List<T> findAll(FilterBase filter, PageRequestParam pageRequest) {
        Pageable pageable = PaginationUtils.getPageRequest(pageRequest);
        return getRepository().findAll(toPredicate(filter), pageable).toList();
    }

    default List<T> findAll(FilterBase filter, PageRequestParam pageRequest, Collection<String> joins) {
        ReportCondition condition = generateReport(filter);
        if (joins != null) {
            joins.forEach(condition::addJoinReport);
        }
        Pageable pageable = PaginationUtils.getPageRequest(pageRequest);
        return getRepository().findAll(toPredicate(condition), pageable).toList();
    }

    default List<T> findAll(ReportCondition condition, PageRequestParam pageRequest) {
        Pageable pageable = PaginationUtils.getPageRequest(pageRequest);
        return getRepository().findAll(toPredicate(condition), pageable).toList();
    }

    default Page<T> findAllPage(FilterBase filter, PageRequestParam pageRequest, Collection<String> joins) {
        ReportCondition condition = generateReport(filter);
        if (joins != null) {
            joins.forEach(condition::addJoinReport);
        }
        Pageable pageable = PaginationUtils.getPageRequest(pageRequest);
        return getRepository().findAll(toPredicate(condition), pageable);
    }

    default Page<T> findAllPage(FilterBase filter, PageRequestParam pageRequest) {
        Pageable pageable = PaginationUtils.getPageRequest(pageRequest);
        return getRepository().findAll(toPredicate(filter), pageable);
    }

    default Page<T> findAllPage(ReportCondition condition, PageRequestParam pageRequest) {
        Pageable pageable = PaginationUtils.getPageRequest(pageRequest);
        return getRepository().findAll(toPredicate(condition), pageable);
    }

    // ============= AGGREGATION METHODS =============
    
    /**
     * Count entities with conditions
     */
    default long count(ReportCondition condition) {
        return getRepository().count(toPredicate(condition));
    }
    
    default long count(FilterBase filter) {
        return getRepository().count(toPredicate(filter));
    }
    
    default long count(Map<String, Object> equalConditions) {
        ReportCondition condition = new ReportCondition();
        equalConditions.forEach(condition::addEqual);
        return getRepository().count(toPredicate(condition));
    }

    // ============= EXISTENCE METHODS =============
    
    /**
     * Check if entity exists with conditions
     */
    default boolean exists(ReportCondition condition) {
        return getRepository().count(toPredicate(condition)) > 0;
    }
    
    default boolean exists(FilterBase filter) {
        return getRepository().count(toPredicate(filter)) > 0;
    }
    
    default boolean exists(Map<String, Object> equalConditions) {
        ReportCondition condition = new ReportCondition();
        equalConditions.forEach(condition::addEqual);
        return getRepository().count(toPredicate(condition)) > 0;
    }
    
    default boolean existsById(Object id) {
        return getRepository().count(getById(id, new ArrayList<>())) > 0;
    }

    // ============= SINGLE RESULT METHODS =============
    
    /**
     * Find first entity matching conditions
     */
    default Optional<T> findFirst(ReportCondition condition) {
        PageRequestParam pageRequest = PageRequestParam.of(0, 1);
        List<T> results = findAll(condition, pageRequest);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
    
    default Optional<T> findFirst(FilterBase filter) {
        ReportCondition condition = generateReport(filter);
        return findFirst(condition);
    }
    
    default Optional<T> findFirst(Map<String, Object> equalConditions) {
        ReportCondition condition = new ReportCondition();
        equalConditions.forEach(condition::addEqual);
        return findFirst(condition);
    }

    // ============= BATCH OPERATIONS =============
    
    /**
     * Find all entities by IDs
     */
    default List<T> findAllByIds(List<Object> ids) {
        return getRepository().findAll(getAllByIds(ids, new ArrayList<>()));
    }
    
    default List<T> findAllByIds(List<Object> ids, Collection<String> joins) {
        List<JoinReport> joinReports = joins != null ? joins.stream().map(JoinReport::of).toList() : new ArrayList<>();
        return getRepository().findAll(getAllByIds(ids, joinReports));
    }

    // ============= UTILITY METHODS =============
    
    /**
     * Create a new ReportCondition for fluent API usage
     */
    default ReportCondition newCondition() {
        return new ReportCondition();
    }
    
    /**
     * Create a new ReportFilter for fluent API usage
     */
    default ReportFilter newFilter() {
        return new ReportFilter();
    }
    
    /**
     * Create JoinReport from string
     */
    default JoinReport join(String path) {
        return JoinReport.of(path);
    }
    
    /**
     * Create multiple JoinReports from strings
     */
    default List<JoinReport> joins(String... paths) {
        return Arrays.stream(paths).map(JoinReport::of).toList();
    }

    // ============= SEARCH METHODS =============
    
    /**
     * Search entities with text in multiple fields
     */
    default List<T> search(String searchText, List<String> searchFields, PageRequestParam pageRequest) {
        ReportCondition condition = new ReportCondition();
        ReportFilter orFilter = new ReportFilter();
        
        for (String field : searchFields) {
            orFilter.addContainsIgnoreCase(field, searchText);
        }
        
        condition.setOrFilter(orFilter);
        return findAll(condition, pageRequest);
    }
    
    default Page<T> searchPage(String searchText, List<String> searchFields, PageRequestParam pageRequest) {
        ReportCondition condition = new ReportCondition();
        ReportFilter orFilter = new ReportFilter();
        
        for (String field : searchFields) {
            orFilter.addContainsIgnoreCase(field, searchText);
        }
        
        condition.setOrFilter(orFilter);
        return findAllPage(condition, pageRequest);
    }

    // ============= BUILDER METHODS =============
    
    /**
     * Start building a condition with fluent API
     */
    default ReportCondition where(String field, Object value) {
        return newCondition().addEqual(field, value);
    }
    
    default ReportCondition whereLike(String field, String value) {
        return newCondition().addLike(field, value);
    }
    
    default ReportCondition whereIn(String field, Collection<?> values) {
        return newCondition().addIn(field, values);
    }
    
    default ReportCondition whereBetween(String field, Object start, Object end) {
        return newCondition().addBetween(field, start, end);
    }
    
    default ReportCondition whereGreaterThan(String field, Object value) {
        return newCondition().addGreaterThan(field, value);
    }
    
    default ReportCondition whereLessThan(String field, Object value) {
        return newCondition().addLessThan(field, value);
    }
}