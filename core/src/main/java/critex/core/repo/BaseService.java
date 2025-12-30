package critex.core.repo;

import critex.core.utility.CustomError;
import critex.core.utility.CustomException;
import critex.core.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.*;

public interface BaseService<T> extends FilterableSpecificationGenerator<T> {

    JpaSpecificationExecutor<T> getRepository();

    default <T> T getEntityById(Object id, Collection<String> joins) {
        if (id == null) {
            throw new CustomException(1472, "id.can.not.be.null", CustomError.ILLEGAL_REQUEST);
        }
        List<JoinReport> joinReports = joins != null ? joins.stream().map(JoinReport::of).toList() : new ArrayList<>();
        List<T> result = (List<T>) getRepository().findAll(getById(id, joinReports));
        if (!result.isEmpty()) {
            return (T) result.getFirst();
        }
        throw new CustomException(1471, "entity.not.found", CustomError.NOT_FOUND);
    }

    default <T> T getEntityByIdInnerJoin(Object id, Collection<JoinReport> joins) {
        if (id == null) {
            throw new CustomException(1472, "id.can.not.be.null", CustomError.ILLEGAL_REQUEST);
        }
        List<T> result = (List<T>) getRepository().findAll(getById(id, joins));
        if (!result.isEmpty()) {
            return (T) result.getFirst();
        }
        throw new CustomException(1471, "entity.not.found", CustomError.NOT_FOUND);
    }

    default <T> Optional<T> getEntityOptionalById(Object id, Collection<String> joins) {
        if (id == null) {
            throw new CustomException(1472, "id.can.not.be.null", CustomError.ILLEGAL_REQUEST);
        }
        List<JoinReport> joinReports = joins != null ? joins.stream().map(JoinReport::of).toList() : new ArrayList<>();
        List<T> result = (List<T>) getRepository().findAll(getById(id, joinReports));
        return Optional.ofNullable(result.isEmpty() ? null : result.getFirst());
    }

    default List<T> getListByEqualConditions(Map<String, Object> condition, Collection<String> joins) {
        List<JoinReport> joinReports = joins != null ? joins.stream().map(JoinReport::of).toList() : new ArrayList<>();
        ReportCondition reportCondition = new ReportCondition();
        condition.forEach(reportCondition::addEqual);
        joinReports.forEach(reportCondition::addJoinReport);
        return getRepository().findAll(toPredicate(reportCondition));
    }

    default List<T> getListByEqualConditionsInnerJoin(Map<String, Object> condition, Collection<JoinReport> joins) {
        ReportCondition reportCondition = new ReportCondition();
        condition.forEach(reportCondition::addEqual);
        joins.forEach(reportCondition::addJoinReport);
        return getRepository().findAll(toPredicate(reportCondition));
    }

    default List<T> getListByConditionsInnerJoin(ReportCondition mainCondition, Collection<JoinReport> joins) {
        // Add joins
        if (joins != null) {
            joins.forEach(mainCondition::addJoinReport);
        }

        return getRepository().findAll(toPredicate(mainCondition));


    }

    default T getByEqualConditionsInnerJoin(Map<String, Object> condition, Collection<JoinReport> joins) {
        List<T> result = getListByEqualConditionsInnerJoin(condition, joins);
        if (result.isEmpty()) {
            throw new CustomException(1472, "entity.not.found", CustomError.NOT_FOUND);
        }
        return (T) result.getFirst();
    }

    default T getByConditionsInnerJoin(ReportCondition mainCondition , Collection<JoinReport> joins) {
        List<T> result = getListByConditionsInnerJoin(mainCondition, joins);
        if (result.isEmpty()) {
            throw new CustomException(1472, "entity.not.found", CustomError.NOT_FOUND);
        }
        return (T) result.getFirst();
    }

    default List<T> findAll(FilterBase filter, PageRequestParam pageRequest) {
        return getRepository().findAll(toPredicate(filter), PaginationUtils.getPageRequest(pageRequest)).toList();
    }

    default List<T> findAll(FilterBase filter, PageRequestParam pageRequest, Collection<String> joins) {
        ReportCondition condition = generateReport(filter);
        if (joins != null) {
            joins.forEach(condition::addJoinReport);
        }
        return getRepository()
                .findAll(toPredicate(condition), PaginationUtils.getPageRequest(pageRequest))
                .toList();
    }

    default List<T> findAll(ReportCondition condition, PageRequestParam pageRequest) {
        return getRepository().findAll(toPredicate(condition), PaginationUtils.getPageRequest(pageRequest)).toList();
    }

    default Page<T> findAllPage(FilterBase filter, PageRequestParam pageRequest, Collection<String> joins) {
        ReportCondition condition = generateReport(filter);
        if (joins != null) {
            joins.forEach(condition::addJoinReport);
        }
        return getRepository().findAll(toPredicate(condition), PaginationUtils.getPageRequest(pageRequest));
    }

    default Page<T> findAllPage(FilterBase filter, PageRequestParam pageRequest) {
        return getRepository().findAll(toPredicate(filter), PaginationUtils.getPageRequest(pageRequest));
    }

    default Page<T> findAllPage(ReportCondition condition, PageRequestParam pageRequest) {
        return getRepository().findAll(toPredicate(condition), PaginationUtils.getPageRequest(pageRequest));
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
        PageRequestParam pageRequest = PageRequestParam.builder()
                .pageNumber(0)
                .pageSize(1)
                .build();
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

//    other methods
}
