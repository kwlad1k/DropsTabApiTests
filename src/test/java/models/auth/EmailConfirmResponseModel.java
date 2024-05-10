package models.auth;

import lombok.Data;

@Data
public class EmailConfirmResponseModel {

    String status, code, data, message;

    Boolean success;
}
