package critex.core.model;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

public class PaginationUtils {
    
    public static Pageable getPageRequest(PageRequestParam pageRequestParam) {
        if (pageRequestParam == null) {
            return PageRequest.of(0, 10);
        }
        
        Sort sort = Sort.unsorted();
        if (pageRequestParam.getSortBy() != null && !pageRequestParam.getSortBy().isEmpty()) {
            Sort.Direction direction = "desc".equalsIgnoreCase(pageRequestParam.getSortDirection()) 
                ? Sort.Direction.DESC 
                : Sort.Direction.ASC;
            sort = Sort.by(direction, pageRequestParam.getSortBy());
        }
        
        return PageRequest.of(pageRequestParam.getPageNumber(), pageRequestParam.getPageSize(), sort);
    }
    
    public static Pageable getPageRequest(int page, int size) {
        return PageRequest.of(page, size);
    }
    
    public static Pageable getPageRequest(int page, int size, Sort sort) {
        return PageRequest.of(page, size, sort);
    }
    
    public static Pageable getPageRequest(int page, int size, String sortBy, String sortDirection) {
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDirection) 
            ? Sort.Direction.DESC 
            : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortBy);
        return PageRequest.of(page, size, sort);
    }
    
    public static Sort createSort(List<SortField> sortFields) {
        if (sortFields == null || sortFields.isEmpty()) {
            return Sort.unsorted();
        }
        
        List<Sort.Order> orders = sortFields.stream()
            .map(SortField::toOrder)
            .toList();
            
        return Sort.by(orders);
    }
}