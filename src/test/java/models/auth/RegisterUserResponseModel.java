package models.auth;

import lombok.Data;

@Data
public class RegisterUserResponseModel {

    String status, code, data, message;

    Boolean success;
}
