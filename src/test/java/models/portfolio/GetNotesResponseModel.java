package models.portfolio;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetNotesResponseModel {

    List<Note> content;

    Pageable pageable;

    Boolean last, first, empty;

    Integer totalPages, totalElements, size, number;

    Sort sort;

    Integer numberOfElements;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Pageable {

        Sort sort;

        Integer offset, pageNumber, pageSize;

        Boolean paged, unpaged;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Sort {

        Boolean empty, unsorted, sorted;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Note {

        Integer id;

        String note;

        Long updatedAt;
    }
}
