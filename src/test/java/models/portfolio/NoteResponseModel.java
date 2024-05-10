package models.portfolio;

import lombok.Data;

@Data
public class NoteResponseModel {

    String note, status;

    Long id, updatedAt;
}
