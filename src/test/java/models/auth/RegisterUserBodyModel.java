package models.auth;

import lombok.Data;

@Data
public class RegisterUserBodyModel {

    String email, password, username;
}
