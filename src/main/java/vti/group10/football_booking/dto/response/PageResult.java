package vti.group10.football_booking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
    public class PageResult<T> {
        private List<T> content;
        private long totalElements;
        private int totalPages;
        private int currentPage;
        private int pageSize;
    }
