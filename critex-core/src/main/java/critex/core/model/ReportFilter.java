package critex.core.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Setter
@Getter
public class ReportFilter {
    private List<ConditionParameter> parameters = new ArrayList<>();
    private ReportFilter orFilter;

    public ReportFilter setOrFilter(ReportFilter orFilter) {
        this.orFilter = orFilter;
        return this;
    }

    public ReportFilter addConditionParameter(ConditionParameter parameter) {
        this.parameters.add(parameter);
        return this;
    }

    private void addFilter(ConditionParameter conditionParameter) {
        this.addConditionParameter(conditionParameter);
    }

    public void addEqual(String key, Object value) {
        addFilter(ConditionParameter.of(key, value, Operator.EQUALS));
    }

    public void addNotEqual(String key, Object value) {
        addFilter(ConditionParameter.of(key, value, Operator.NOT_EQUALS));
    }

    public void addGreaterThan(String key, Object value) {
        addFilter(ConditionParameter.of(key, value, Operator.GREATER_THAN));
    }

    public void addLessThan(String key, Object value) {
        addFilter(ConditionParameter.of(key, value, Operator.LESS_THAN));
    }

    public void addGreaterEquals(String key, Object value) {
        addFilter(ConditionParameter.of(key, value, Operator.GREATER_EQUALS));
    }

    public void addLessEquals(String key, Object value) {
        addFilter(ConditionParameter.of(key, value, Operator.LESS_EQUALS));
    }

    public void addIn(String key, Object value) {
        addFilter(ConditionParameter.of(key, value, Operator.IN));
    }

    public void addNotIn(String key, Object value) {
        addFilter(ConditionParameter.of(key, value, Operator.NOT_IN));
    }

    public <T extends Number> void addMax(String key, T value) {
        addFilter(ConditionParameter.of(key, value, Operator.LESS_EQUALS));
    }

    public <T extends Number> void addMin(String key, T value) {
        addFilter(ConditionParameter.of(key, value, Operator.GREATER_EQUALS));
    }

    public void addMaxDate(String key, Object value) {
        addFilter(ConditionParameter.of(key, value, Operator.LESS_EQUALS_TIME));
    }

    public void addMinDate(String key, Object value) {
        addFilter(ConditionParameter.of(key, value, Operator.GREATER_EQUALS_TIME));
    }

    public void addIn(String key, Collection<?> value) {
        addFilter(ConditionParameter.of(key, value, Operator.IN));
    }

    public void addNotIn(String key, Collection<?> value) {
        addFilter(ConditionParameter.of(key, value, Operator.NOT_IN));
    }

    public void addLike(String key, String value) {
        addFilter(ConditionParameter.of(key, value, Operator.LIKE));
    }

    public void addLikeIgnoreCase(String key, String value) {
        addFilter(ConditionParameter.of(key, value, Operator.LIKE_IGNORE_CASE));
    }

    public void addNull(String key) {
        addFilter(ConditionParameter.of(key, null, Operator.NULL));
    }

    public void addNotNull(String key) {
        addFilter(ConditionParameter.of(key, null, Operator.NOT_NULL));
    }

    public void addBetween(String key, Object startValue, Object endValue) {
        addFilter(ConditionParameter.of(key, new Object[]{startValue, endValue}, Operator.BETWEEN));
    }

    public void addNotBetween(String key, Object startValue, Object endValue) {
        addFilter(ConditionParameter.of(key, new Object[]{startValue, endValue}, Operator.NOT_BETWEEN));
    }

    public void addStartsWith(String key, String value) {
        addFilter(ConditionParameter.of(key, value, Operator.STARTS_WITH));
    }

    public void addEndsWith(String key, String value) {
        addFilter(ConditionParameter.of(key, value, Operator.ENDS_WITH));
    }

    public void addContains(String key, String value) {
        addFilter(ConditionParameter.of(key, value, Operator.CONTAINS));
    }

    public void addNotContains(String key, String value) {
        addFilter(ConditionParameter.of(key, value, Operator.NOT_CONTAINS));
    }

    public void addStartsWithIgnoreCase(String key, String value) {
        addFilter(ConditionParameter.of(key, value, Operator.STARTS_WITH_IGNORE_CASE));
    }

    public void addEndsWithIgnoreCase(String key, String value) {
        addFilter(ConditionParameter.of(key, value, Operator.ENDS_WITH_IGNORE_CASE));
    }

    public void addContainsIgnoreCase(String key, String value) {
        addFilter(ConditionParameter.of(key, value, Operator.CONTAINS_IGNORE_CASE));
    }

    public void addNotContainsIgnoreCase(String key, String value) {
        addFilter(ConditionParameter.of(key, value, Operator.NOT_CONTAINS_IGNORE_CASE));
    }

    public void addGreaterThanDate(String key, Object value) {
        addFilter(ConditionParameter.of(key, value, Operator.GREATER_THAN_TIME));
    }

    public void addLessThanDate(String key, Object value) {
        addFilter(ConditionParameter.of(key, value, Operator.LESS_THAN_TIME));
    }

    public void addIsEmpty(String key) {
        addFilter(ConditionParameter.of(key, null, Operator.IS_EMPTY));
    }

    public void addIsNotEmpty(String key) {
        addFilter(ConditionParameter.of(key, null, Operator.IS_NOT_EMPTY));
    }

    public void addSizeEquals(String key, Integer size) {
        addFilter(ConditionParameter.of(key, size, Operator.SIZE_EQUALS));
    }

    public void addSizeGreaterThan(String key, Integer size) {
        addFilter(ConditionParameter.of(key, size, Operator.SIZE_GREATER_THAN));
    }

    public void addSizeLessThan(String key, Integer size) {
        addFilter(ConditionParameter.of(key, size, Operator.SIZE_LESS_THAN));
    }

    public void addSizeGreaterEquals(String key, Integer size) {
        addFilter(ConditionParameter.of(key, size, Operator.SIZE_GREATER_EQUALS));
    }

    public void addSizeLessEquals(String key, Integer size) {
        addFilter(ConditionParameter.of(key, size, Operator.SIZE_LESS_EQUALS));
    }

    /**
     * Add date range condition (between two dates)
     */
    public void addDateRange(String key, Object startDate, Object endDate) {
        addMinDate(key, startDate);
        addMaxDate(key, endDate);
    }

    /**
     * Add numeric range condition (between two numbers)
     */
    public <T extends Number> void addNumericRange(String key, T min, T max) {
        addMin(key, min);
        addMax(key, max);
    }
}
