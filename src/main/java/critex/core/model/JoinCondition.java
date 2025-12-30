package critex.core.model;


import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class JoinCondition {
    private List<ConditionParameter> conditionParameter = new ArrayList<>();
    private List<JoinCondition> joinConditions = new ArrayList<>();
    private JoinReport join;

    public JoinCondition(JoinReport join) {
        this.join = join;
    }

    public JoinCondition addFilter(ConditionParameter conditionParameter) {
        this.conditionParameter.add(conditionParameter);
        return this;
    }
}
