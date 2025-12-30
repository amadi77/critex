package critex.core.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AggregationField {
    private String fieldName;
    private AggregationType aggregationType;
    private String alias;
    
    public static AggregationField of(String fieldName, AggregationType aggregationType) {
        return new AggregationField(fieldName, aggregationType, null);
    }
    
    public static AggregationField of(String fieldName, AggregationType aggregationType, String alias) {
        return new AggregationField(fieldName, aggregationType, alias);
    }
    
    public static AggregationField count() {
        return new AggregationField("*", AggregationType.COUNT, "count");
    }
    
    public static AggregationField countDistinct(String fieldName) {
        return new AggregationField(fieldName, AggregationType.COUNT_DISTINCT, "countDistinct");
    }
    
    public static AggregationField sum(String fieldName) {
        return new AggregationField(fieldName, AggregationType.SUM, "sum");
    }
    
    public static AggregationField avg(String fieldName) {
        return new AggregationField(fieldName, AggregationType.AVG, "avg");
    }
    
    public static AggregationField max(String fieldName) {
        return new AggregationField(fieldName, AggregationType.MAX, "max");
    }
    
    public static AggregationField min(String fieldName) {
        return new AggregationField(fieldName, AggregationType.MIN, "min");
    }
}