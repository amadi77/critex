package critex.core.model;

import jakarta.validation.constraints.Min;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class PageRequestParam {

    @Min(value = 0, message = "page number must be greater than or equal to 0")
    @Builder.Default
    private int pageNumber = 0;

    @Min(value = 1, message = "page size must be at least 1")
    @Builder.Default
    private int pageSize = 10;

    private String sortBy;

    @Builder.Default
    private String sortDirection = "asc";

    private List<SortField> sortFields;

    public static PageRequestParam of(int pageNumber, int pageSize) {
        return new PageRequestParam(pageNumber, pageSize, null, "asc", null);
    }

    public static PageRequestParam of(int pageNumber, int pageSize, String sortBy) {
        return new PageRequestParam(pageNumber, pageSize, sortBy, "asc", null);
    }

    public static PageRequestParam of(int pageNumber, int pageSize, String sortBy, String sortDirection) {
        return new PageRequestParam(pageNumber, pageSize, sortBy, sortDirection, null);
    }

    public static PageRequestParam of(int pageNumber, int pageSize, List<SortField> sortFields) {
        return new PageRequestParam(pageNumber, pageSize, null, "asc", sortFields);
    }

    public static PageRequestParam firstPage() {
        return of(0, 10);
    }

    public static PageRequestParam firstPage(int size) {
        return of(0, size);
    }

    public static PageRequestParam firstPageSorted(String sortBy) {
        return of(0, 10, sortBy);
    }

    public static PageRequestParam firstPageSorted(String sortBy, String direction) {
        return of(0, 10, sortBy, direction);
    }

    public PageRequestParam withSort(String sortBy) {
        this.sortBy = sortBy;
        return this;
    }

    public PageRequestParam withSort(String sortBy, String direction) {
        this.sortBy = sortBy;
        this.sortDirection = direction;
        return this;
    }

    public PageRequestParam withSortFields(List<SortField> sortFields) {
        this.sortFields = sortFields;
        return this;
    }

    public PageRequestParam nextPage() {
        return of(this.pageNumber + 1, this.pageSize, this.sortBy, this.sortDirection);
    }

    public PageRequestParam previousPage() {
        return of(Math.max(0, this.pageNumber - 1), this.pageSize, this.sortBy, this.sortDirection);
    }

    public PageRequestParam withSize(int size) {
        this.pageSize = size;
        return this;
    }
}
