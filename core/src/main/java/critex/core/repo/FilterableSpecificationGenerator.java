package critex.core.repo;

import critex.core.model.FilterBase;
import critex.core.model.JoinReport;
import critex.core.model.ReportCondition;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collection;

public interface FilterableSpecificationGenerator<T> extends SpecificationGenerator<T>{


    default Specification<T> toPredicate(FilterBase filterBase) {
        return toPredicate(generateReport(filterBase));
    }

    default Specification<T> toPredicate(FilterBase filterBase, Collection<JoinReport> joins) {
        ReportCondition condition = generateReport(filterBase);
        if (joins != null && !joins.isEmpty()) joins.forEach(condition::addJoinReport);

        return toPredicate(condition);
    }

    ReportCondition generateReport(FilterBase filterBase);

}
