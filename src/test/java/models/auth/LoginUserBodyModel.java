package models.auth;

import lombok.Data;

@Data
public class LoginUserBodyModel {

    String email, password;
}
