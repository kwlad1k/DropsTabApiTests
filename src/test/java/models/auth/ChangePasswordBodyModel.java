package models.auth;

import lombok.Data;

@Data
public class ChangePasswordBodyModel {

    String newPassword, oldPassword;
}
