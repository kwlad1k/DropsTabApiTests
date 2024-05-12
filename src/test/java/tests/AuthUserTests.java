package tests;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import models.auth.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;


import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static spec.DefaultSpec.*;
import static spec.AuthUserSpec.*;

@Epic("Управление пользователя")
@Feature("Авторизация")
@DisplayName("Тесты авторизации пользователя")
public class AuthUserTests extends TestBaseAPI {
    TestDataAPI testData = new TestDataAPI();

    @Test
    @Tags({
            @Tag("Positive"),
            @Tag("Auth")
    })
    @Owner("Kwlad1ck")
    @DisplayName("Успешная авторизация пользователя")
    void successfulLoginTest() {
        LoginUserBodyModel loginUserData = new LoginUserBodyModel();
        loginUserData.setEmail(testData.authUserEmail);
        loginUserData.setPassword(testData.authUserPassword);

        LoginUserResponseModel loginUserResponseModel = step("Make request", () ->
                given(defLogSpec)
                        .body(loginUserData)

                        .when()
                        .post("/login")

                        .then()
                        .spec(loginUserResponseSpec)
                        .extract().as(LoginUserResponseModel.class));

        step("Check response", () -> {
            LocalDateTime currentDateTime = LocalDateTime.now();
            LocalDateTime expiresInDateTime = currentDateTime.plus(loginUserResponseModel.getExpiresIn(), ChronoUnit.MILLIS);
            assertThat(loginUserResponseModel.getAccessToken()).isNotNull();
            assertThat(loginUserResponseModel.getRefreshToken()).isNotNull();
            assertThat(expiresInDateTime).isAfterOrEqualTo(currentDateTime);
        });
    }

    @Test
    @Tags({
            @Tag("Negative"),
            @Tag("Auth")
    })
    @Owner("Kwlad1ck")
    @DisplayName("Проверка негативной авторизации пользователя")
    void negativeLLoginTest() {
        LoginUserBodyModel loginUserData = new LoginUserBodyModel();
        loginUserData.setEmail(testData.randomEmail);
        loginUserData.setPassword(testData.authUserPassword);

        LoginUserResponseModel loginUserResponseModel = step("Make request", () ->
                given(defLogSpec)
                        .body(loginUserData)

                        .when()
                        .post("/login")

                        .then()
                        .spec(negativeLoginUserResponseSpec)
                        .extract().as(LoginUserResponseModel.class));

        step("Check response", () -> {
            assertThat(loginUserResponseModel.getSuccess()).isFalse();
            assertThat(loginUserResponseModel.getData()).isNull();
            assertThat(loginUserResponseModel.getCode()).isEqualTo("AUTH_FAILED");
            assertThat(loginUserResponseModel.getMessage()).isEqualTo("Authentication failed");
        });
    }

    @Test
    @Tags({
            @Tag("Positive"),
            @Tag("Auth")
    })
    @Owner("Kwlad1ck")
    @DisplayName("Отправка запроса на регистрацию пользователя")
    void successfulRegistrationTest() {
        RegisterUserBodyModel registerUserData = new RegisterUserBodyModel();
        registerUserData.setEmail(testData.randomEmail);
        registerUserData.setPassword(testData.randomPassword);
        registerUserData.setUsername(testData.randomUserName);

        RegisterUserResponseModel registerUserResponseModel = step("Make request", () ->
                given(defLogSpec)
                        .body(registerUserData)

                        .when()
                        .post("/api/user")

                        .then()
                        .spec(defRespLogSpec)
                        .statusCode(200)
                        .extract().as(RegisterUserResponseModel.class));

        step("Check response", () ->
                assertThat(registerUserResponseModel.getStatus()).isEqualTo("OK"));
    }

    @Test
    @Tags({
            @Tag("Negative"),
            @Tag("Auth")
    })
    @Owner("Kwlad1ck")
    @DisplayName("Отправка запроса на регистраци с невалидным Email")
    void negativeRegistrationWithoutEmailTest() {
        RegisterUserBodyModel registerUserData = new RegisterUserBodyModel();
        registerUserData.setEmail("");
        registerUserData.setPassword(testData.randomPassword);
        registerUserData.setUsername(testData.randomUserName);

        RegisterUserResponseModel registerUserResponseModel = step("Make request", () ->
                given(defLogSpec)
                        .body(registerUserData)

                        .when()
                        .post("/api/user")

                        .then()
                        .spec(defRespLogSpec)
                        .statusCode(400)
                        .extract().as(RegisterUserResponseModel.class));

        step("Check response", () -> {
            assertThat(registerUserResponseModel.getSuccess()).isFalse();
            assertThat(registerUserResponseModel.getData()).isNull();
            assertThat(registerUserResponseModel.getCode()).isEqualTo("EMAIL_VALIDATION");
            assertThat(registerUserResponseModel.getMessage()).isEqualTo("Invalid email");
        });
    }

    @Test
    @Tags({
            @Tag("Negative"),
            @Tag("Auth")
    })
    @Owner("Kwlad1ck")
    @DisplayName("Отправка запроса на регистраци без пароля")
    void negativeRegistrationWithoutPasswordTest() {
        RegisterUserBodyModel registerUserData = new RegisterUserBodyModel();
        registerUserData.setEmail(testData.randomEmail);
        registerUserData.setPassword("");
        registerUserData.setUsername(testData.randomUserName);

        RegisterUserResponseModel registerUserResponseModel = step("Make request", () ->
                given(defLogSpec)
                        .body(registerUserData)

                        .when()
                        .post("/api/user")

                        .then()
                        .spec(defRespLogSpec)
                        .statusCode(400)
                        .extract().as(RegisterUserResponseModel.class));

        step("Check response", () -> {
            assertThat(registerUserResponseModel.getSuccess()).isFalse();
            assertThat(registerUserResponseModel.getData()).isNull();
            assertThat(registerUserResponseModel.getCode()).isEqualTo("PASSWORD_CHARACTER_MISMATCH");
            assertThat(registerUserResponseModel.getMessage()).isEqualTo("Invalid character in password");
        });
    }

    @Test
    @Tags({
            @Tag("Negative"),
            @Tag("Auth")
    })
    @Owner("Kwlad1ck")
    @DisplayName("Отправка запроса регистрации c длинным именем пользователя")
    void negativeRegistrationWithInCorrectUserNameTest() {
        RegisterUserBodyModel registerUserData = new RegisterUserBodyModel();
        registerUserData.setEmail(testData.randomEmail);
        registerUserData.setPassword(testData.randomPassword);
        registerUserData.setUsername(testData.randomWrongUserName);

        RegisterUserResponseModel registerUserResponseModel = step("Make request", () ->
                given(defLogSpec)
                        .body(registerUserData)

                        .when()
                        .post("/api/user")

                        .then()
                        .spec(defRespLogSpec)
                        .statusCode(500)
                        .extract().as(RegisterUserResponseModel.class));

        step("Check response", () -> {
            assertThat(registerUserResponseModel.getSuccess()).isFalse();
            assertThat(registerUserResponseModel.getData()).isNull();
            assertThat(registerUserResponseModel.getCode()).isEqualTo("USERNAME_WRONG_LENGTH");
            assertThat(registerUserResponseModel.getMessage()).isEqualTo("Username must be from 3 to 32 characters long");
        });
    }

    @Test
    @Tags({
            @Tag("Negative"),
            @Tag("Auth")
    })
    @Owner("Kwlad1ck")
    @DisplayName("Запрос на регистрацию с уже существующим Email в системе")
    void negativeRegistrationWithEmailTest() {
        RegisterUserBodyModel registerUserData = new RegisterUserBodyModel();
        registerUserData.setEmail(testData.authUserEmail);
        registerUserData.setPassword(testData.randomPassword);
        registerUserData.setUsername(testData.randomUserName);

        RegisterUserResponseModel registerUserResponseModel = step("Make request", () ->
                given(defLogSpec)
                        .body(registerUserData)

                        .when()
                        .post("/api/user")

                        .then()
                        .spec(defRespLogSpec)
                        .statusCode(400)
                        .extract().as(RegisterUserResponseModel.class));

        step("Check response", () -> {
            assertThat(registerUserResponseModel.getCode()).isEqualTo("EMAIL_ALREADY_EXISTS");
            assertThat(registerUserResponseModel.getData()).isNull();
            assertThat(registerUserResponseModel.getMessage()).isEqualTo("User with this email already exists");
            assertThat(registerUserResponseModel.getSuccess()).isFalse();
        });
    }

    @Test
    @Tags({
            @Tag("Negative"),
            @Tag("Auth")
    })
    @Owner("Kwlad1ck")
    @DisplayName("Запрос на регистрацию с уже существующим Username в системе")
    void negativeRegistrationWithUserNameTest() {
        RegisterUserBodyModel registerUserData = new RegisterUserBodyModel();
        registerUserData.setEmail(testData.randomEmail);
        registerUserData.setPassword(testData.randomPassword);
        registerUserData.setUsername(testData.authUserName);

        RegisterUserResponseModel registerUserResponseModel = step("Make request", () ->
                given(defLogSpec)
                        .body(registerUserData)

                        .when()
                        .post("/api/user")

                        .then()
                        .spec(defRespLogSpec)
                        .statusCode(400)
                        .extract().as(RegisterUserResponseModel.class));

        step("Check response", () -> {
            assertThat(registerUserResponseModel.getCode()).isEqualTo("USERNAME_ALREADY_EXISTS");
            assertThat(registerUserResponseModel.getData()).isNull();
            assertThat(registerUserResponseModel.getMessage()).isEqualTo("User with this username already exists");
            assertThat(registerUserResponseModel.getSuccess()).isFalse();
        });
    }

    @Test
    @Tags({
            @Tag("Positive"),
            @Tag("Auth")
    })
    @Owner("Kwlad1ck")
    @DisplayName("Отправка письма подтвержденния существующего аккаунта")
    void successfulResendEmailConfirmTest() {
        EmailConfirmBodyModel emailConfirmData = new EmailConfirmBodyModel();
        emailConfirmData.setEmail(testData.authUserEmail);

        EmailConfirmResponseModel emailConfirmResponseModel = step("Make request", () ->
                given(defLogSpec)
                        .body(emailConfirmData)

                        .when()
                        .post("/api/user/resendEmailConfirm")

                        .then()
                        .spec(defRespLogSpec)
                        .statusCode(200)
                        .extract().as(EmailConfirmResponseModel.class));

        step("Check response", () ->
                assertThat(emailConfirmResponseModel.getStatus()).isEqualTo("OK"));
    }

    @Test
    @Tags({
            @Tag("Negative"),
            @Tag("Auth")
    })
    @Owner("Kwlad1ck")
    @DisplayName("Отправка письма подтвержденния на несуществующий Email")
    void negativeResendEmailConfirmTest() {
        EmailConfirmBodyModel emailConfirmData = new EmailConfirmBodyModel();
        emailConfirmData.setEmail(testData.randomEmail);

        EmailConfirmResponseModel emailConfirmResponseModel = step("Make request", () ->
                given(defLogSpec)
                        .body(emailConfirmData)

                        .when()
                        .post("/api/user/resendEmailConfirm")

                        .then()
                        .spec(defRespLogSpec)
                        .statusCode(404)
                        .extract().as(EmailConfirmResponseModel.class));

        step("Check response", () -> {
            assertThat(emailConfirmResponseModel.getSuccess()).isFalse();
            assertThat(emailConfirmResponseModel.getData()).isNull();
            assertThat(emailConfirmResponseModel.getCode()).isEqualTo("NOT_FOUND");
            assertThat(emailConfirmResponseModel.getMessage()).isEqualTo("Not found");

        });
    }

    @Test
    @Tags({
            @Tag("Positive"),
            @Tag("Auth")
    })
    @Owner("Kwlad1ck")
    @DisplayName("Выслать ссылку восстановления пароля на почту юзеру")
    void resetPasswordTest() {
        ResetPasswordModel resetPasswordData = new ResetPasswordModel();
        resetPasswordData.setEmail(testData.randomEmail);

        EmailConfirmResponseModel emailConfirmResponseModel = step("Make request", () ->
                given(defLogSpec)
                        .body(resetPasswordData)

                        .when()
                        .post("/api/user/resetPassword")

                        .then()
                        .spec(defRespLogSpec)
                        .statusCode(200)
                        .extract().as(EmailConfirmResponseModel.class));

        step("Check response", () ->
                assertThat(emailConfirmResponseModel.getStatus()).isEqualTo("OK"));
    }

    @Test
    @Tags({
            @Tag("Positive"),
            @Tag("Auth")
    })
    @Owner("Kwlad1ck")
    @DisplayName("Проверка Username на сущестование")
    void successfulCheckUserNameExistsTest() {
        CheckUserNameBodyModel checkUserNameData = new CheckUserNameBodyModel();
        checkUserNameData.setUsername("Kwlad1ck");

        CheckUserNameResponseModel checkUserNameResponseModel = step("Make request", () ->
                given(defLogSpec)
                        .body(checkUserNameData)

                        .when()
                        .post("/api/user/checkUsernameExistence")

                        .then()
                        .spec(defRespLogSpec)
                        .statusCode(200)
                        .extract().as(CheckUserNameResponseModel.class));

        step("Check response", () ->
                assertThat(checkUserNameResponseModel.getExists()).isTrue());
    }

    @Test
    @Tags({
            @Tag("Negative"),
            @Tag("Auth")
    })
    @Owner("Kwlad1ck")
    @DisplayName("Проверка Username что он не сущестует")
    void negativeCheckUserNameExistsTest() {
        CheckUserNameBodyModel checkUserNameData = new CheckUserNameBodyModel();
        checkUserNameData.setUsername(testData.randomWrongUserName);

        CheckUserNameResponseModel checkUserNameResponseModel = step("Make request", () ->
                given(defLogSpec)
                        .body(checkUserNameData)

                        .when()
                        .post("/api/user/checkUsernameExistence")

                        .then()
                        .spec(defRespLogSpec)
                        .statusCode(200)
                        .extract().as(CheckUserNameResponseModel.class));

        step("Check response", () ->
                assertThat(checkUserNameResponseModel.getExists()).isFalse());
    }

    @Test
    @Tags({
            @Tag("Positive"),
            @Tag("Auth")
    })
    @Owner("Kwlad1ck")
    @DisplayName("Запрос возвращения текущего пользователя")
    void checkUserCurrentTest() {
        CurrentResponseModel currentResponseModel = step("Make request", () ->
                given(defLogSpec)
                        .header("Authorization", "Bearer " + testData.authorizationToken)

                        .when()
                        .get("/api/user/current")

                        .then()
                        .spec(currentResponseSpec)
                        .extract().as(CurrentResponseModel.class));

        step("Check response", () -> {
            assertThat(currentResponseModel.getEmail()).isEqualTo(testData.authUserEmail);
            assertThat(currentResponseModel.getUsername()).isEqualTo(testData.authUserName);
        });
    }

    @Test
    @Tags({
            @Tag("Positive"),
            @Tag("Auth")
    })
    @Owner("Kwlad1ck")
    @DisplayName("Запрос возвращения неавторизованной учетки")
    void negativeCheckUserCurrentTest() {
        CurrentResponseModel currentResponseModel = step("Make request", () ->
                given(defLogSpec)

                        .when()
                        .get("/api/user/current")

                        .then()
                        .spec(defRespLogSpec)
                        .statusCode(403)
                        .extract().as(CurrentResponseModel.class));

        step("Check response", () -> {
            assertThat(currentResponseModel.getSuccess()).isFalse();
            assertThat(currentResponseModel.getData()).isNull();
            assertThat(currentResponseModel.getCode()).isEqualTo("ACCESS_DENIED");
            assertThat(currentResponseModel.getMessage()).isEqualTo("Access denied");
        });
    }

    @Test
    @Tags({
            @Tag("Positive"),
            @Tag("Auth")
    })
    @Owner("Kwlad1ck")
    @DisplayName("Успешная смена пароля аккаунта")
    void successfulChangePasswordTest() {
        ChangePasswordBodyModel changePasswordData = new ChangePasswordBodyModel();
        changePasswordData.setOldPassword("112233");
        changePasswordData.setNewPassword("112233");

        ChangePasswordResponseModel changePasswordResponseModel = step("Make request", () ->
                given(defLogSpec)
                        .body(changePasswordData)
                        .header("Authorization", "Bearer " + testData.authorizationToken)

                        .when()
                        .post("/api/user/changePassword")

                        .then()
                        .spec(defRespLogSpec)
                        .statusCode(200)
                        .extract().as(ChangePasswordResponseModel.class));

        step("Check response", () -> {
            assertThat(changePasswordResponseModel.getAccessToken()).isNotNull();
            assertThat(changePasswordResponseModel.getExpiresIn()).isNotNull();
        });
    }

    @Test
    @Tags({
            @Tag("Negative"),
            @Tag("Auth")
    })
    @Owner("Kwlad1ck")
    @DisplayName("Не успешная смена пароля акканута")
    void negativeChangePasswordTest() {
        ChangePasswordBodyModel changePasswordData = new ChangePasswordBodyModel();
        changePasswordData.setOldPassword("123123");
        changePasswordData.setNewPassword("112233");

        ChangePasswordResponseModel changePasswordResponseModel = step("Make request", () ->
                given(defLogSpec)
                        .body(changePasswordData)
                        .header("Authorization", "Bearer " + testData.authorizationToken)

                        .when()
                        .post("/api/user/changePassword")

                        .then()
                        .spec(defRespLogSpec)
                        .statusCode(400)
                        .extract().as(ChangePasswordResponseModel.class));

        step("Check response", () -> {
            assertThat(changePasswordResponseModel.getSuccess()).isFalse();
            assertThat(changePasswordResponseModel.getData()).isNull();
            assertThat(changePasswordResponseModel.getCode()).isEqualTo("OLD_PASSWORD_INVALID");
            assertThat(changePasswordResponseModel.getMessage()).isEqualTo("Old password is invalid");
        });
    }
}


