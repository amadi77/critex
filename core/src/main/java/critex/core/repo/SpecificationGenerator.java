package critex.core.repo;

import critex.core.model.*;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.*;

public abstract class SpecificationGenerator<T> extends Parameterized<T> {

    protected Collection<JoinReport> toJoin(Collection<String> joins) {
        if (joins == null || joins.isEmpty()) return Collections.emptyList();
        Map<String, JoinReport> joinsReport = new HashMap<>();
        for (String eachJoin : joins) {
            String[] split = eachJoin.split("\\.");
            JoinReport joinReport = JoinReport.of(split[0]);
            joinReport = checkDuplicateJoin(joinsReport, split[0], joinReport);
            for (int i = 1; i < split.length; i++) {
                JoinReport innerJoin = JoinReport.of(split[i]);
                joinReport.addJoin(innerJoin);
                joinReport = innerJoin;
            }
        }
        return joinsReport.values();
    }

    protected static JoinReport checkDuplicateJoin(Map<String, JoinReport> joinsReport, String key, JoinReport joinReport) {
        if (joinsReport.containsKey(key)) {
            joinReport = joinsReport.get(key);
        } else {
            joinsReport.put(key, joinReport);
        }
        return joinReport;
    }

    protected Specification<T> getById(Object id, Collection<JoinReport> joins) {
        ReportCondition condition = new ReportCondition();
        condition.addEqual("id", id);
        joins.forEach(condition::addJoinReport);
        return toPredicate(condition);
    }

    protected Specification<T> getAllByIds(List<Object> ids, Collection<JoinReport> joins) {
        ReportCondition condition = new ReportCondition();
        condition.addIn("id", ids);
        joins.forEach(condition::addJoinReport);
        return toPredicate(condition);
    }

    protected Specification<T> toPredicate(ReportCondition report) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = getPredicate(report, root, query, criteriaBuilder);
            Map<Predicate, Boolean> predicates = new HashMap<>();
            generateJoinAndInnerPredicate(report, root, query, criteriaBuilder, predicates);
            if (!predicates.isEmpty()) {
                return applyInnerPredicate(criteriaBuilder, predicates, predicate);
            }
            return predicate;
        };
    }

    protected static Predicate applyInnerPredicate(CriteriaBuilder criteriaBuilder, Map<Predicate, Boolean> predicates, Predicate predicate) {
        List<Predicate> andPredicates = predicates.entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).toList();
        List<Predicate> orPredicates = predicates.entrySet().stream().filter((item) -> Boolean.FALSE.equals(item.getValue())).map(Map.Entry::getKey).toList();
        List<Predicate> innerPredicates = new ArrayList<>();

        if (!andPredicates.isEmpty()) {
            Predicate and = criteriaBuilder.and(andPredicates.toArray(new Predicate[0]));
            innerPredicates.add(and);
        }
        if (!orPredicates.isEmpty()) {
            Predicate or = criteriaBuilder.or(orPredicates.toArray(new Predicate[0]));
            innerPredicates.add(or);
        }
        Predicate innerPredicate = criteriaBuilder.and(innerPredicates.toArray(new Predicate[0]));
        return predicate != null ? criteriaBuilder.and(predicate, innerPredicate) : innerPredicate;
    }

    protected void generateJoinAndInnerPredicate(ReportCondition report, Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder, Map<Predicate, Boolean> predicates) {
        if (query.getResultType().isAssignableFrom(Long.class) || report.isDeActiveFetch()) {
            for (JoinReport eachJoin : report.getJoins()) {
                generateJoin(root, criteriaBuilder, predicates, eachJoin);
            }
        } else {
            for (JoinReport eachJoin : report.getJoins()) {
                if (eachJoin.isFetch()) generateFetch(root, criteriaBuilder, predicates, eachJoin);
                else generateJoin(root, criteriaBuilder, predicates, eachJoin);
            }
        }
    }

    protected void generateFetch(Root<T> root, CriteriaBuilder criteriaBuilder, Map<Predicate, Boolean> predicates, JoinReport eachJoin) {
        Fetch fetch = root.fetch(eachJoin.getKey(), eachJoin.getJoinType());
        if (eachJoin.getInnerJoin() != null && !eachJoin.getInnerJoin().isEmpty()) {
            eachJoin.getInnerJoin().forEach(eachInnerFetch -> addInnerFetch(fetch, eachInnerFetch, criteriaBuilder, predicates));
        }
        if (eachJoin.getFilter() != null) {
            Predicate predicate = generateInnerPredicateJoin((Join) fetch, eachJoin.getFilter(), criteriaBuilder, true);
            if (predicate != null) {
                predicates.put(predicate, true);
            }
            if (eachJoin.getFilter().getOrFilter() != null) {
                Predicate orPredicate = generateInnerPredicateJoin((Join) fetch, eachJoin.getFilter().getOrFilter(), criteriaBuilder, false);
                if (orPredicate != null) {
                    predicates.put(orPredicate, false);
                }
            }
        }
    }

    protected void generateJoin(Root<T> root, CriteriaBuilder criteriaBuilder, Map<Predicate, Boolean> predicates, JoinReport eachJoin) {
        Join join = root.join(eachJoin.getKey(), eachJoin.getJoinType());
        if (eachJoin.getInnerJoin() != null && !eachJoin.getInnerJoin().isEmpty()) {
            eachJoin.getInnerJoin().forEach(eachInnerJoin -> addInnerJoin(join, eachInnerJoin, criteriaBuilder, predicates));
        }
        if (eachJoin.getFilter() != null) {
            Predicate predicate = generateInnerPredicateJoin(join, eachJoin.getFilter(), criteriaBuilder, true);
            if (predicate != null) {
                predicates.put(predicate, true);
            }
            if (eachJoin.getFilter().getOrFilter() != null) {
                Predicate orPredicate = generateInnerPredicateJoin(join, eachJoin.getFilter().getOrFilter(), criteriaBuilder, false);
                if (orPredicate != null) {
                    predicates.put(orPredicate, false);
                }
            }
        }
    }

    protected void addInnerFetch(Fetch fetch, JoinReport joinReport, CriteriaBuilder criteriaBuilder, Map<Predicate, Boolean> predicates) {
        Fetch innerFetch = fetch.fetch(joinReport.getKey(), joinReport.getJoinType());
        if (joinReport.getFilter() != null) {
            Predicate predicate = generateInnerPredicateJoin((Join) innerFetch, joinReport.getFilter(), criteriaBuilder, true);
            if (predicate != null) {
                predicates.put(predicate, true);
            }
            if (joinReport.getFilter().getOrFilter() != null) {
                Predicate orPredicate = generateInnerPredicateJoin((Join) fetch, joinReport.getFilter().getOrFilter(), criteriaBuilder, false);
                if (orPredicate != null) {
                    predicates.put(orPredicate, false);
                }
            }
        }
        if (joinReport.getInnerJoin() != null && !joinReport.getInnerJoin().isEmpty()) {
            joinReport.getInnerJoin().forEach(eachInnerFetch -> addInnerFetch(innerFetch, eachInnerFetch, criteriaBuilder, predicates));
        }
    }

    protected void addInnerJoin(Join parent, JoinReport joinReport, CriteriaBuilder criteriaBuilder, Map<Predicate, Boolean> predicates) {
        Join innerJoin = parent.join(joinReport.getKey(), joinReport.getJoinType());
        if (joinReport.getFilter() != null) {
            Predicate predicate = generateInnerPredicateJoin(innerJoin, joinReport.getFilter(), criteriaBuilder, true);
            if (predicate != null) {
                predicates.put(predicate, true);
            }
            if (joinReport.getFilter().getOrFilter() != null) {
                Predicate orPredicate = generateInnerPredicateJoin(innerJoin, joinReport.getFilter().getOrFilter(), criteriaBuilder, false);
                if (orPredicate != null) {
                    predicates.put(orPredicate, false);
                }
            }
        }
        if (joinReport.getInnerJoin() != null && !joinReport.getInnerJoin().isEmpty()) {
            joinReport.getInnerJoin().forEach(eachInnerJoin -> addInnerJoin(innerJoin, eachInnerJoin, criteriaBuilder, predicates));
        }
    }

    protected Predicate getPredicate(ReportCondition report, Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

        query.distinct(report.isDistinct());
        Predicate mainPredicate = generatePredicate(report.getFilter(), root, criteriaBuilder, true);
        if (report.getFilter().getOrFilter() != null) {
            Predicate orPredicate = generatePredicate(report.getFilter().getOrFilter(), root, criteriaBuilder, false);
            return mainPredicate != null ? criteriaBuilder.and(mainPredicate, orPredicate) : orPredicate;
        }
        return mainPredicate;
    }

    protected Predicate generatePredicate(ReportFilter filter, Root<T> root, CriteriaBuilder criteriaBuilder, boolean isAnd) {
        List<Predicate> predicates = new ArrayList<>();
        for (ConditionParameter eachConditionParameter : filter.getParameters()) {
            if (eachConditionParameter.getValue() != null || List.of(Operator.NULL, Operator.NOT_NULL).contains(eachConditionParameter.getOperator())) {
                Predicate predicate = generatePredicate(root, criteriaBuilder, eachConditionParameter);
                if (predicate != null) {
                    predicates.add(predicate);
                }
            }
        }
        if (predicates.isEmpty()) {
            return null;
        }
        if (isAnd) {
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        } else {
            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        }

    }

    protected Predicate generateInnerPredicateJoin(Join join, ReportFilter filter, CriteriaBuilder criteriaBuilder, boolean and) {
        List<Predicate> predicates = new ArrayList<>();

        for (ConditionParameter eachParam : filter.getParameters()) {
            if (eachParam.getValue() != null || List.of(Operator.NULL, Operator.NOT_NULL).contains(eachParam.getOperator())) {
                Predicate correspondingPredicate = getCorrespondingPredicate(criteriaBuilder, eachParam, join.get(eachParam.getKey()));
                if (correspondingPredicate != null) {
                    predicates.add(correspondingPredicate);
                }
            }
        }
        if (and) {
            return predicates.isEmpty() ? null : criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }
        return predicates.isEmpty() ? null : criteriaBuilder.or(predicates.toArray(new Predicate[0]));
    }

    protected Predicate generatePredicate(Root<T> root, CriteriaBuilder criteriaBuilder, ConditionParameter eachConditionParameter) {
        String[] key = eachConditionParameter.getKey().split("\\.");
        Path path = root.get(key[0]);
        for (int i = 1; i < key.length; i++) {
            String eachKey = key[i];
            path = path.get(eachKey);
        }
        return getCorrespondingPredicate(criteriaBuilder, eachConditionParameter, path);
    }

    protected static Predicate getCorrespondingPredicate(CriteriaBuilder criteriaBuilder, ConditionParameter eachConditionParameter, Path path) {
        Predicate predicate = null;
        switch (eachConditionParameter.getOperator()) {
            case LIKE -> predicate = criteriaBuilder.like(path, "%" + eachConditionParameter.getValue() + "%");
            case LIKE_IGNORE_CASE ->
                    predicate = criteriaBuilder.like(criteriaBuilder.lower(path), "%" + eachConditionParameter.getValue().toString().toLowerCase() + "%");
            case EQUALS -> predicate = criteriaBuilder.equal(path, eachConditionParameter.getValue());
            case NOT_EQUALS -> predicate = criteriaBuilder.notEqual(path, eachConditionParameter.getValue());
            case LESS_EQUALS -> predicate = criteriaBuilder.le(path, (Number) eachConditionParameter.getValue());
            case GREATER_EQUALS -> predicate = criteriaBuilder.ge(path, (Number) eachConditionParameter.getValue());
            case LESS_EQUALS_TIME ->
                    predicate = criteriaBuilder.lessThanOrEqualTo(path, (Comparable) eachConditionParameter.getValue());
            case GREATER_EQUALS_TIME ->
                    predicate = criteriaBuilder.greaterThanOrEqualTo(path, (Comparable) eachConditionParameter.getValue());
            case GREATER_THAN -> predicate = criteriaBuilder.gt(path, (Number) eachConditionParameter.getValue());
            case GREATER_THAN_TIME ->
                    predicate = criteriaBuilder.greaterThan(path, (Comparable) eachConditionParameter.getValue());
            case LESS_THAN -> predicate = criteriaBuilder.lt(path, (Number) eachConditionParameter.getValue());
            case LESS_THAN_TIME ->
                    predicate = criteriaBuilder.lessThan(path, (Comparable) eachConditionParameter.getValue());
            case IN -> predicate = path.in((Collection<?>) eachConditionParameter.getValue());
            case NOT_IN -> predicate = criteriaBuilder.not(path.in((Collection<?>) eachConditionParameter.getValue()));
            case NULL -> predicate = criteriaBuilder.isNull(path);
            case NOT_NULL -> predicate = criteriaBuilder.isNotNull(path);
            case BETWEEN -> {
                Object[] values = (Object[]) eachConditionParameter.getValue();
                predicate = criteriaBuilder.between(path, (Comparable) values[0], (Comparable) values[1]);
            }
            case NOT_BETWEEN -> {
                Object[] values = (Object[]) eachConditionParameter.getValue();
                predicate = criteriaBuilder.not(criteriaBuilder.between(path, (Comparable) values[0], (Comparable) values[1]));
            }
            case STARTS_WITH -> predicate = criteriaBuilder.like(path, eachConditionParameter.getValue() + "%");
            case ENDS_WITH -> predicate = criteriaBuilder.like(path, "%" + eachConditionParameter.getValue());
            case CONTAINS -> predicate = criteriaBuilder.like(path, "%" + eachConditionParameter.getValue() + "%");
            case NOT_CONTAINS -> predicate = criteriaBuilder.not(criteriaBuilder.like(path, "%" + eachConditionParameter.getValue() + "%"));
            case STARTS_WITH_IGNORE_CASE ->
                    predicate = criteriaBuilder.like(criteriaBuilder.lower(path), eachConditionParameter.getValue().toString().toLowerCase() + "%");
            case ENDS_WITH_IGNORE_CASE ->
                    predicate = criteriaBuilder.like(criteriaBuilder.lower(path), "%" + eachConditionParameter.getValue().toString().toLowerCase());
            case CONTAINS_IGNORE_CASE ->
                    predicate = criteriaBuilder.like(criteriaBuilder.lower(path), "%" + eachConditionParameter.getValue().toString().toLowerCase() + "%");
            case NOT_CONTAINS_IGNORE_CASE ->
                    predicate = criteriaBuilder.not(criteriaBuilder.like(criteriaBuilder.lower(path), "%" + eachConditionParameter.getValue().toString().toLowerCase() + "%"));
            case IS_EMPTY -> predicate = criteriaBuilder.isEmpty(path);
            case IS_NOT_EMPTY -> predicate = criteriaBuilder.isNotEmpty(path);
            case SIZE_EQUALS -> predicate = criteriaBuilder.equal(criteriaBuilder.size(path), (Integer) eachConditionParameter.getValue());
            case SIZE_GREATER_THAN -> predicate = criteriaBuilder.gt(criteriaBuilder.size(path), (Integer) eachConditionParameter.getValue());
            case SIZE_LESS_THAN -> predicate = criteriaBuilder.lt(criteriaBuilder.size(path), (Integer) eachConditionParameter.getValue());
            case SIZE_GREATER_EQUALS -> predicate = criteriaBuilder.ge(criteriaBuilder.size(path), (Integer) eachConditionParameter.getValue());
            case SIZE_LESS_EQUALS -> predicate = criteriaBuilder.le(criteriaBuilder.size(path), (Integer) eachConditionParameter.getValue());
        }
        return predicate;
    }


}
