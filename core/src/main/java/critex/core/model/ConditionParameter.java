package critex.core.model;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConditionParameter {
    private String key;
    private Object value;
    private Operator operator;


    public static ConditionParameter of(String key, Object value, Operator operator) {
        return new ConditionParameter(key, value, operator);
    }
}
