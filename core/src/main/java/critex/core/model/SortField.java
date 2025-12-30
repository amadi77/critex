package critex.core.model;

import lombok.*;
import org.springframework.data.domain.Sort;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SortField {
    private String fieldName;
    private Sort.Direction direction;
    private boolean ignoreCase;
    
    public static SortField asc(String fieldName) {
        return new SortField(fieldName, Sort.Direction.ASC, false);
    }
    
    public static SortField desc(String fieldName) {
        return new SortField(fieldName, Sort.Direction.DESC, false);
    }
    
    public static SortField ascIgnoreCase(String fieldName) {
        return new SortField(fieldName, Sort.Direction.ASC, true);
    }
    
    public static SortField descIgnoreCase(String fieldName) {
        return new SortField(fieldName, Sort.Direction.DESC, true);
    }
    
    public static SortField of(String fieldName, Sort.Direction direction) {
        return new SortField(fieldName, direction, false);
    }
    
    public static SortField of(String fieldName, Sort.Direction direction, boolean ignoreCase) {
        return new SortField(fieldName, direction, ignoreCase);
    }
    
    public Sort.Order toOrder() {
        Sort.Order order = new Sort.Order(direction, fieldName);
        if (ignoreCase) {
            order = order.ignoreCase();
        }
        return order;
    }
}