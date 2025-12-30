package critex.core.model;

import jakarta.persistence.criteria.JoinType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@Builder
public class JoinReport {
    private String key;
    private JoinType joinType = JoinType.LEFT;
    private ReportFilter filter = new ReportFilter();
    private Set<JoinReport> innerJoin = new HashSet<>();
    private boolean fetch = true;

    public JoinReport(String key, JoinType joinType, ReportFilter filter, Set<JoinReport> innerJoin, boolean fetch) {
        this.joinType = joinType;
        this.filter = filter;
        this.fetch = fetch;
        this.innerJoin = innerJoin;

        String[] newJoins = key.split("\\.");
        this.key = newJoins[0];
        if (newJoins.length > 1) {
            JoinReport joinReport = JoinReport.of(newJoins[1]);
            this.addJoin(joinReport);
            for (int i = 2; i < newJoins.length; i++) {
                JoinReport newJoin = JoinReport.of(newJoins[i]);
                joinReport.addJoin(newJoin);
                joinReport = newJoin;
            }
        }
    }

    public static JoinReport of(String key, JoinType joinType) {
        return new JoinReport(key, joinType, new ReportFilter(), new HashSet<>(), true);
    }

    public static JoinReport of(String key, JoinType joinType, boolean fetch) {
        return new JoinReport(key, joinType, new ReportFilter(), new HashSet<>(), fetch);
    }

    public static JoinReport of(String key) {
        return new JoinReport(key, JoinType.LEFT, new ReportFilter(), new HashSet<>(), true);
    }

    public JoinReport addJoin(JoinReport join) {
        this.innerJoin.stream().filter(join::equals)
                .findFirst()
                .ifPresentOrElse(item -> {
                            join.getInnerJoin().forEach(join::addJoin);
                        }
                        , () -> this.innerJoin.add(join));
        return this;
    }

    public JoinReport addJoin(String join) {
        this.innerJoin.add(JoinReport.of(join));
        return this;
    }

    public ReportFilter filter(){
        return this.filter;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JoinReport that = (JoinReport) o;
        return Objects.equals(key, that.key) && Objects.equals(joinType, that.joinType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, joinType);
    }
}
