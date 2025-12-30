package critex.core.model;

import jakarta.persistence.criteria.JoinType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportCondition {
    private ReportFilter filter = new ReportFilter();
    private Set<JoinReport> joins = new HashSet<>();
    private boolean distinct;
    private boolean deActiveFetch = false;

    public void setDeActiveFetch(boolean deActiveFetch) {
        this.deActiveFetch = deActiveFetch;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    private ReportCondition addFilter(ConditionParameter conditionParameter) {
        this.filter.addConditionParameter(conditionParameter);
        return this;
    }

    public ReportFilter setOrFilter(ReportFilter orFilter) {
        return this.filter.setOrFilter(orFilter);
    }

    public ReportCondition addEqual(String key, Object value) {
        return addFilter(ConditionParameter.of(key, value, Operator.EQUALS));
    }

    public ReportCondition addNotEqual(String key, Object value) {
        return addFilter(ConditionParameter.of(key, value, Operator.NOT_EQUALS));
    }

    public <T extends Number> ReportCondition addMax(String key, T value) {
        return addFilter(ConditionParameter.of(key, value, Operator.LESS_EQUALS));
    }

    public <T extends Number> ReportCondition addMin(String key, T value) {
        return addFilter(ConditionParameter.of(key, value, Operator.GREATER_EQUALS));
    }

    public ReportCondition addMaxDate(String key, Object value) {
        return addFilter(ConditionParameter.of(key, value, Operator.LESS_EQUALS_TIME));
    }

    public ReportCondition addMinDate(String key, Object value) {
        return addFilter(ConditionParameter.of(key, value, Operator.GREATER_EQUALS_TIME));
    }

    public ReportCondition addIn(String key, Collection<?> value) {
        return addFilter(ConditionParameter.of(key, value, Operator.IN));
    }

    public ReportCondition addNotIn(String key, Collection<?> value) {
        return addFilter(ConditionParameter.of(key, value, Operator.NOT_IN));
    }

    public ReportCondition addLike(String key, String value) {
        return addFilter(ConditionParameter.of(key, value, Operator.LIKE));
    }

    public ReportCondition addLikeIgnoreCase(String key, String value) {
        return addFilter(ConditionParameter.of(key, value, Operator.LIKE_IGNORE_CASE));
    }

    public ReportCondition addNull(String key) {
        return addFilter(ConditionParameter.of(key, null, Operator.NULL));
    }

    public ReportCondition addNotNull(String key) {
        return addFilter(ConditionParameter.of(key, null, Operator.NOT_NULL));
    }

    public ReportCondition addBetween(String key, Object startValue, Object endValue) {
        return addFilter(ConditionParameter.of(key, new Object[]{startValue, endValue}, Operator.BETWEEN));
    }

    public ReportCondition addNotBetween(String key, Object startValue, Object endValue) {
        return addFilter(ConditionParameter.of(key, new Object[]{startValue, endValue}, Operator.NOT_BETWEEN));
    }

    public ReportCondition addStartsWith(String key, String value) {
        return addFilter(ConditionParameter.of(key, value, Operator.STARTS_WITH));
    }

    public ReportCondition addEndsWith(String key, String value) {
        return addFilter(ConditionParameter.of(key, value, Operator.ENDS_WITH));
    }

    public ReportCondition addContains(String key, String value) {
        return addFilter(ConditionParameter.of(key, value, Operator.CONTAINS));
    }

    public ReportCondition addNotContains(String key, String value) {
        return addFilter(ConditionParameter.of(key, value, Operator.NOT_CONTAINS));
    }

    public ReportCondition addStartsWithIgnoreCase(String key, String value) {
        return addFilter(ConditionParameter.of(key, value, Operator.STARTS_WITH_IGNORE_CASE));
    }

    public ReportCondition addEndsWithIgnoreCase(String key, String value) {
        return addFilter(ConditionParameter.of(key, value, Operator.ENDS_WITH_IGNORE_CASE));
    }

    public ReportCondition addContainsIgnoreCase(String key, String value) {
        return addFilter(ConditionParameter.of(key, value, Operator.CONTAINS_IGNORE_CASE));
    }

    public ReportCondition addNotContainsIgnoreCase(String key, String value) {
        return addFilter(ConditionParameter.of(key, value, Operator.NOT_CONTAINS_IGNORE_CASE));
    }

    public ReportCondition addGreaterThan(String key, Object value) {
        return addFilter(ConditionParameter.of(key, value, Operator.GREATER_THAN));
    }

    public ReportCondition addLessThan(String key, Object value) {
        return addFilter(ConditionParameter.of(key, value, Operator.LESS_THAN));
    }

    public ReportCondition addGreaterThanDate(String key, Object value) {
        return addFilter(ConditionParameter.of(key, value, Operator.GREATER_THAN_TIME));
    }

    public ReportCondition addLessThanDate(String key, Object value) {
        return addFilter(ConditionParameter.of(key, value, Operator.LESS_THAN_TIME));
    }

    public ReportCondition addIsEmpty(String key) {
        return addFilter(ConditionParameter.of(key, null, Operator.IS_EMPTY));
    }

    public ReportCondition addIsNotEmpty(String key) {
        return addFilter(ConditionParameter.of(key, null, Operator.IS_NOT_EMPTY));
    }

    public ReportCondition addSizeEquals(String key, Integer size) {
        return addFilter(ConditionParameter.of(key, size, Operator.SIZE_EQUALS));
    }

    public ReportCondition addSizeGreaterThan(String key, Integer size) {
        return addFilter(ConditionParameter.of(key, size, Operator.SIZE_GREATER_THAN));
    }

    public ReportCondition addSizeLessThan(String key, Integer size) {
        return addFilter(ConditionParameter.of(key, size, Operator.SIZE_LESS_THAN));
    }

    public ReportCondition addSizeGreaterEquals(String key, Integer size) {
        return addFilter(ConditionParameter.of(key, size, Operator.SIZE_GREATER_EQUALS));
    }

    public ReportCondition addSizeLessEquals(String key, Integer size) {
        return addFilter(ConditionParameter.of(key, size, Operator.SIZE_LESS_EQUALS));
    }

    /**
     * Add date range condition (between two dates)
     */
    public ReportCondition addDateRange(String key, Object startDate, Object endDate) {
        return addMinDate(key, startDate).addMaxDate(key, endDate);
    }

    /**
     * Add numeric range condition (between two numbers)
     */
    public <T extends Number> ReportCondition addNumericRange(String key, T min, T max) {
        return addMin(key, min).addMax(key, max);
    }

    public JoinReport addJoinReport(JoinReport joinReport) {
        Optional<JoinReport> first = this.joins.stream().filter(join -> join.equals(joinReport)).findFirst();
        first.ifPresentOrElse(join -> {
            joinReport.getInnerJoin().forEach(join::addJoin);
        }, () -> this.joins.add(joinReport));
        return first.orElse(joinReport);
    }

    public JoinReport addJoinReport(String key, JoinType join) {
        String[] newJoins = key.split("\\.");
        JoinReport main = JoinReport.of(newJoins[0], join);
        List<JoinReport> joins = new ArrayList<>();
        joins.add(main);
        for (int i = 1; i < newJoins.length; i++) {
            JoinReport innerJoin = JoinReport.of(newJoins[i], join);
            main.addJoin(innerJoin);
            main = innerJoin;
        }
        return addJoinReport(joins.getFirst());
    }

    /**
     * you can use this like this:
     * parent.child.child
     * and it will work
     */
    public JoinReport addJoinReport(String key) {
        return addJoinReport(key, JoinType.LEFT);
    }
}
