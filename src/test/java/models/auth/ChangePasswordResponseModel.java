package models.auth;

import lombok.Data;

@Data
public class ChangePasswordResponseModel {

    String accessToken, expiresIn, refreshToken;

    Boolean success;

    String data, code, message;
}
