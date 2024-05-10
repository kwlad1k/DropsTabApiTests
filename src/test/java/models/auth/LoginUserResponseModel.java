package models.auth;

import lombok.Data;

@Data
public class LoginUserResponseModel {

    String accessToken, refreshToken, data, code, message;

    Long expiresIn;

    Boolean success;
}
