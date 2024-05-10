package tests;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import models.portfolio.*;
import org.junit.jupiter.api.*;

import java.util.Collections;
import java.util.List;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static spec.DefaultSpec.*;
import static spec.PortfolioSpec.*;
import static utils.RandomUtils.*;

@Epic("Управление активами")
@Feature("Портфолио")
@DisplayName("Тесты портфолио")
public class PortfolioTests extends TestBaseAPI {
    TestDataAPI testData = new TestDataAPI();

    @Test
    @Tag("Portfolio")
    @Owner("Kwlad1ck")
    @DisplayName("Получение списка портфолио аккаунта")
    void successfulResponseShortRequestTest() {
        PortfolioResponseModel portfolioResponseModel = step("Получение списка портфолио аккаунта", () ->
                given(defLogWithAuthSpec)

                        .when()
                        .get("/api/portfolioGroup/short")

                        .then()
                        .spec(defRespLogSpec)
                        .statusCode(200)
                        .extract().as(PortfolioResponseModel.class));

        step("Check response", () ->
                assertThat(portfolioResponseModel).isNotEqualTo(Collections.emptyList()));
    }

    @Test
    @Tag("Portfolio")
    @Owner("Kwlad1ck")
    @DisplayName("Создание нового портфолио для аккаунта")
    void successfulCreateNewPortfolioTest() {

        PortfolioBodyModel createPortfolioData = new PortfolioBodyModel();
        createPortfolioData.setColor(testData.randomColor);
        createPortfolioData.setDescription(testData.randomAddDescription);
        createPortfolioData.setIncludeInTotal(testData.randomBoolean);
        createPortfolioData.setName(testData.randomPortfolioName);

        PortfolioResponseModel portfolioResponseModel = step("Make request", () ->
                given(defLogWithAuthSpec)
                        .body(createPortfolioData)

                        .when()
                        .post("/api/portfolioGroup")

                        .then()
                        .spec(defRespLogSpec)
                        .statusCode(200)
                        .extract().as(PortfolioResponseModel.class));

        step("Check response", () -> {
            assertThat(portfolioResponseModel.getOptions().getColor()).isEqualTo(testData.randomColor);
            assertThat(portfolioResponseModel.getDescription()).isEqualTo(testData.randomAddDescription);
            assertThat(portfolioResponseModel.getOptions().getIncludeInTotal()).isEqualTo(testData.randomBoolean);
            assertThat(portfolioResponseModel.getName()).isEqualTo(testData.randomPortfolioName);
        });
    }

    @RepeatedTest(2)
    @Tag("Portfolio")
    @Owner("Kwlad1ck")
    @DisplayName("Удаление рандомного портфолио аккаунта")
    void successfulDeleteRandomPortfolio() {
        Response response = step("Получение списка портфолио аккаунта", () ->
                given(defLogWithAuthSpec)

                        .when()
                        .get("/api/portfolioGroup/short")

                        .then()
                        .spec(defRespLogSpec)
                        .statusCode(200)
                        .extract().response());

        JsonPath jsonPath = response.jsonPath();
        List<Integer> userPortfolioId = jsonPath.getList("portfolioGroups.id");
        Integer randomPortfolioId = getRandomValueExcluding(userPortfolioId, testData.excludePortfolio);

        DeleteResponseModel deleteResponseModel = step("Запрос удаления портфолио", () ->
                given(defLogWithAuthSpec)

                        .when()
                        .delete("/api/portfolioGroup/" + randomPortfolioId)

                        .then()
                        .spec(defRespLogSpec)
                        .statusCode(200)
                        .extract().as(DeleteResponseModel.class));

        step("Check response", () -> {
            assertThat(deleteResponseModel.getStatus()).isEqualTo("OK");
        });
    }

    @Test
    @Tag("Portfolio")
    @Owner("Kwlad1ck")
    @DisplayName("Изменение имени, описания, цвета и включеня/исключения в тотал портфолио")
    void successfulChangeSettingsPortfolioTest() {
        Response response = step("Получение списка портфолио аккаунта", () ->
                given(defLogWithAuthSpec)

                        .when()
                        .get("/api/portfolioGroup/short")

                        .then()
                        .spec(defRespLogSpec)
                        .statusCode(200)
                        .extract().response());

        JsonPath jsonPath = response.jsonPath();
        List<Integer> userPortfolioId = jsonPath.getList("portfolioGroups.id");
        Integer randomPortfolioId = getRandomValueExcluding(userPortfolioId, testData.excludePortfolio);

        PortfolioBodyModel PortfolioData = new PortfolioBodyModel();
        PortfolioData.setColor(testData.randomColor);
        PortfolioData.setDescription(testData.randomChangeDescription);
        PortfolioData.setGroupId(randomPortfolioId);
        PortfolioData.setIncludeInTotal(testData.randomBoolean);
        PortfolioData.setName(testData.randomPortfolioName);

        PortfolioResponseModel portfolioResponseModel = step("Изменение настроек выбранного портфолио", () ->
                given(defLogWithAuthSpec)
                        .body(PortfolioData)

                        .when()
                        .post("/api/portfolioGroup/rename")

                        .then()
                        .spec(defRespLogSpec)
                        .statusCode(200)
                        .extract().as(PortfolioResponseModel.class));

        step("Check response", () -> {
            assertThat(portfolioResponseModel.getOptions().getColor()).isEqualTo(testData.randomColor);
            assertThat(portfolioResponseModel.getDescription()).isEqualTo(testData.randomChangeDescription);
            assertThat(portfolioResponseModel.getOptions().getIncludeInTotal()).isEqualTo(testData.randomBoolean);
            assertThat(portfolioResponseModel.getName()).isEqualTo(testData.randomPortfolioName);
        });
    }

    @Test
    @Tag("Portfolio")
    @Owner("Kwlad1ck")
    @DisplayName("Измнения типа шеринга портфолио")
    void successfulChangeSharingTypePortfolioTest() {
        Response response = step("Получение списка портфолио аккаунта", () ->
                given(defLogWithAuthSpec)

                        .when()
                        .get("/api/portfolioGroup/short")

                        .then()
                        .spec(defRespLogSpec)
                        .extract().response());

        JsonPath jsonPath = response.jsonPath();
        List<Integer> userPortfolioId = jsonPath.getList("portfolioGroups.id");
        Integer randomPortfolioId = getRandomValueExcluding(userPortfolioId, testData.excludePortfolio);

        SharePortfolioBodyModel shareTypeData = new SharePortfolioBodyModel();
        shareTypeData.setType(testData.randomSharingTypePortfolio);
        SharePortfolioResponseModel sharePortfolioResponseModel = step("Изменение Sharing Type для потрфлио", () ->
                given(defLogWithAuthSpec)
                        .body(shareTypeData)

                        .when()
                        .post("/api/portfolioGroup/" + randomPortfolioId + "/token/type")

                        .then()
                        .spec(defRespLogSpec)
                        .statusCode(200)
                        .extract().as(SharePortfolioResponseModel.class));

        step("Check response", () -> (
                assertThat(sharePortfolioResponseModel.getType()).isEqualTo(testData.randomSharingTypePortfolio)));
    }

    @Test
    @Tag("Portfolio")
    @Owner("Kwlad1ck")
    @DisplayName("Измнения ссылки шеринга портфолио")
    void successfulChangeSharingLinkTest() {
        SharePortfolioBodyModel shareTypeData = new SharePortfolioBodyModel();
        shareTypeData.setSharingSlug(testData.randomSharingSlug);

        SharePortfolioResponseModel sharePortfolioResponseModel = step("Изменение ссылки шеринга портфолио", () ->
                given(defLogWithAuthSpec)
                        .body(shareTypeData)

                        .when()
                        .put("/api/portfolioGroup/" + testData.specialPortfolioId + "/token/custom")

                        .then()
                        .spec(defRespLogSpec)
                        .statusCode(200)
                        .extract().as(SharePortfolioResponseModel.class));

        step("Check response", () -> (
                assertThat(sharePortfolioResponseModel.getSharingSlug())
                        .containsIgnoringCase(testData.randomSharingSlug)));
    }

    @Test
    @Tag("Portfolio")
    @Owner("Kwlad1ck")
    @DisplayName("Изменение ссылки шеринга портфолио невалидным количеством символов")
    void unsuccessfulChangeSharingWithInvalidLinkTest() {
        Response response = step("Получение списка портфолио аккаунта", () ->
                given(defLogWithAuthSpec)

                        .when()
                        .get("/api/portfolioGroup/short")

                        .then()
                        .spec(defRespLogSpec)
                        .statusCode(200)
                        .extract().response());

        JsonPath jsonPath = response.jsonPath();
        List<Integer> userPortfolioId = jsonPath.getList("portfolioGroups.id");
        Integer randomPortfolioId = getRandomValueExcluding(userPortfolioId, testData.excludePortfolio);

        SharePortfolioBodyModel shareTypeData = new SharePortfolioBodyModel();
        shareTypeData.setSharingSlug(testData.invalidSharingSlug);

        SharePortfolioResponseModel sharePortfolioResponseModel = step("Изменение ссылки шеринга портфолио", () ->
                given(defLogWithAuthSpec)
                        .body(shareTypeData)

                        .when()
                        .put("/api/portfolioGroup/" + randomPortfolioId + "/token/custom")

                        .then()
                        .spec(defRespLogSpec)
                        .statusCode(400)
                        .extract().as(SharePortfolioResponseModel.class));

        step("Check response", () -> {
            assertThat(sharePortfolioResponseModel.getSuccess()).isFalse();
            assertThat(sharePortfolioResponseModel.getData()).isNull();
            assertThat(sharePortfolioResponseModel.getCode()).isEqualTo("SHARING_SLUG_LENGTH_EXCEPTION");
            assertThat(sharePortfolioResponseModel.getMessage()).isEqualTo("User custom token must be from 1 to 32 character long");
        });
    }

    @Test
    @Tag("Portfolio")
    @Owner("Kwlad1ck")
    @DisplayName("Изменение ссылки шеринга портфолио с запретными словами")
    void unsuccessfulChangeSharingWithBanedWordLinkTest() {
        SharePortfolioBodyModel shareTypeData = new SharePortfolioBodyModel();
        shareTypeData.setSharingSlug(testData.forbiddenWordsForSharingSlug);

        SharePortfolioResponseModel sharePortfolioResponseModel = step("Изменение ссылки шеринга портфолио", () ->
                given(defLogWithAuthSpec)
                        .body(shareTypeData)

                        .when()
                        .put("/api/portfolioGroup/" + testData.specialPortfolioId + "/token/custom")

                        .then()
                        .spec(defRespLogSpec)
                        .statusCode(400)
                        .extract().as(SharePortfolioResponseModel.class));

        step("Check response", () -> {
            assertThat(sharePortfolioResponseModel.getSuccess()).isFalse();
            assertThat(sharePortfolioResponseModel.getData()).isNull();
            assertThat(sharePortfolioResponseModel.getCode()).isEqualTo("SHARING_FORBIDDEN_WORD_EXCEPTION");
            assertThat(sharePortfolioResponseModel.getMessage()).isEqualTo("The name of your entity contains a forbidden word: " + testData.forbiddenWordsForSharingSlug);
        });
    }

    @Test
    @Tag("Portfolio")
    @Owner("Kwlad1ck")
    @DisplayName("Изменение ссылки шеринга портфоило c символами кирилицы")
    void unsuccessfulChangeSharingCyrillicSymbolsLinkTest() {
        Response response = step("Получение списка портфолио аккаунта", () ->
                given(defLogWithAuthSpec)

                        .when()
                        .get("/api/portfolioGroup/short")

                        .then()
                        .spec(defRespLogSpec)
                        .statusCode(200)
                        .extract().response());

        JsonPath jsonPath = response.jsonPath();
        List<Integer> userPortfolioId = jsonPath.getList("portfolioGroups.id");
        Integer randomPortfolioId = getRandomValueExcluding(userPortfolioId, testData.excludePortfolio);

        SharePortfolioBodyModel shareTypeData = new SharePortfolioBodyModel();
        shareTypeData.setSharingSlug(testData.cyrillicSharingSlug);

        SharePortfolioResponseModel sharePortfolioResponseModel = step("Изменение ссылки шеринга портфолио", () ->
                given(defLogWithAuthSpec)
                        .body(shareTypeData)

                        .when()
                        .put("/api/portfolioGroup/" + randomPortfolioId + "/token/custom")

                        .then()
                        .spec(defRespLogSpec)
                        .statusCode(400)
                        .extract().as(SharePortfolioResponseModel.class));

        step("Check response", () -> {
            assertThat(sharePortfolioResponseModel.getSuccess()).isFalse();
            assertThat(sharePortfolioResponseModel.getData()).isNull();
            assertThat(sharePortfolioResponseModel.getCode()).isEqualTo("SHARING_SLUG_SPECIAL_CHARACTER_EXCEPTION");
            assertThat(sharePortfolioResponseModel.getMessage())
                    .isEqualTo("Invalid sharingSlug, reason: Special character exist");
        });
    }

    @Test
    @Tag("Portfolio")
    @Owner("Kwlad1ck")
    @DisplayName("Изменение внутренних настроек портфолио")
    void successfulChangeOptionsPortfolioSettingsTest() {
        Response response = step("Получение списка портфолио аккаунта", () ->
                given(defLogWithAuthSpec)

                        .when()
                        .get("/api/portfolioGroup/short")

                        .then()
                        .spec(defRespLogSpec)
                        .statusCode(200)
                        .extract().response());

        JsonPath jsonPath = response.jsonPath();
        List<Integer> userPortfolioId = jsonPath.getList("portfolioGroups.id");
        Integer randomPortfolioId = getRandomValueExcluding(userPortfolioId, testData.excludePortfolio);

        OptionsPortfolioBodyModel optionsPortfolioData = new OptionsPortfolioBodyModel();
        optionsPortfolioData.setChartTimeframe(testData.randomChartTimeFrame);
        optionsPortfolioData.setColor(testData.randomColor);
        optionsPortfolioData.setGroupId(randomPortfolioId);
        optionsPortfolioData.setIncludeInTotal(testData.rdmBlnInclTotal);
        optionsPortfolioData.setPortfolioTimeframe(testData.randomPortfolioTimeframe);
        optionsPortfolioData.setShowChart(testData.rdmBlnShowChart);
        optionsPortfolioData.setShowHoldingsShareChart(testData.rdmBlnShowHoldingsShareChart);
        optionsPortfolioData.setShowNotes(testData.rdmBlnShowNotes);
        optionsPortfolioData.setShowSmallHoldings(testData.rdmBlnShowSmallHoldings);
        optionsPortfolioData.setShowUpcomingEvents(testData.rdmBlnShowUpEvents);
        optionsPortfolioData.setTopPerformance(testData.rdmBlnTopPerformance);

        OptionsPortfolioResponseModel optionsPortfolioResponseModel = step("Изменение параметров Options", () ->
                given(defLogWithAuthSpec)
                        .body(optionsPortfolioData)

                        .when()
                        .post("/api/portfolioGroup/options")

                        .then()
                        .spec(defRespLogSpec)
                        .statusCode(200)
                        .extract().as(OptionsPortfolioResponseModel.class));

        step("Check response", () -> {
            assertThat(optionsPortfolioResponseModel.getChartTimeframe()).isEqualTo(testData.randomChartTimeFrame);
            assertThat(optionsPortfolioResponseModel.getColor()).isEqualTo(testData.randomColor);
            assertThat(optionsPortfolioResponseModel.getIncludeInTotal()).isEqualTo(testData.rdmBlnInclTotal);
            assertThat(optionsPortfolioResponseModel.getPortfolioTimeframe()).isEqualTo(testData.randomPortfolioTimeframe);
            assertThat(optionsPortfolioResponseModel.getShowChart()).isEqualTo(testData.rdmBlnShowChart);
            assertThat(optionsPortfolioResponseModel.getShowHoldingsShareChart()).isEqualTo(testData.rdmBlnShowHoldingsShareChart);
            assertThat(optionsPortfolioResponseModel.getShowNotes()).isEqualTo(testData.rdmBlnShowNotes);
            assertThat(optionsPortfolioResponseModel.getShowSmallHoldings()).isEqualTo(testData.rdmBlnShowSmallHoldings);
            assertThat(optionsPortfolioResponseModel.getShowUpcomingEvents()).isEqualTo(testData.rdmBlnShowUpEvents);
            assertThat(optionsPortfolioResponseModel.getTopPerformance()).isEqualTo(testData.rdmBlnTopPerformance);
        });
    }

    @Test
    @Tag("Portfolio")
    @Owner("Kwlad1ck")
    @DisplayName("Создание дубликата портфолио")
    void successfulCreateDuplicatePortfolioTest() {
        Response response = step("Получение списка портфолио аккаунта", () ->
                given(defLogWithAuthSpec)

                        .when()
                        .get("/api/portfolioGroup/short")

                        .then()
                        .spec(defRespLogSpec)
                        .statusCode(200)
                        .extract().response());

        JsonPath jsonPath = response.jsonPath();
        List<Integer> userPortfolioId = jsonPath.getList("portfolioGroups.id");
        Integer randomPortfolioId = getRandomValueExcluding(userPortfolioId, testData.excludePortfolio);

        PortfolioResponseModel portfolioResponseModel = step("Создание дубликата портфолио", () ->
                given(defLogWithAuthSpec)
                        .queryParam("name", testData.randomDuplicatePortfolioName)

                        .when()
                        .post("/api/portfolioGroup/" + randomPortfolioId + "/copy")

                        .then()
                        .spec(defRespLogSpec)
                        .statusCode(200)
                        .extract().as(PortfolioResponseModel.class));

        step("Check response", () ->
                assertThat(portfolioResponseModel.getName()).isEqualTo(testData.randomDuplicatePortfolioName));
    }

    @Test
    @Tag("Portfolio")
    @Owner("Kwlad1ck")
    @DisplayName("Проверка запроса на получения списка заметок портфолио")
    void notesSheetContainsDataTest() {
        GetNotesResponseModel getNotesResponseModel = step("Полеченеие списка заметок портфолио", () ->
                given(defLogWithAuthSpec)
                        .queryParam("page", 0)
                        .queryParam("size", 15)

                        .when()
                        .get("/api/portfolioGroup/" + testData.specialPortfolioId + "/notes")

                        .then()
                        .spec(notesResponseSpec)
                        .statusCode(200)
                        .extract().as(GetNotesResponseModel.class));

        step("Проверка списка заметок", () -> {
            assertThat(getNotesResponseModel).isNotEqualTo(Collections.emptyList());
            assertThat(getNotesResponseModel.getTotalElements()).isGreaterThan(0);
            assertThat(getNotesResponseModel.getEmpty()).isFalse();
        });
    }

    @Test
    @Tag("Portfolio")
    @Owner("Kwlad1ck")
    @DisplayName("Создание заметки в портфолио")
    void successfulCreateNoteTest() {
        NoteBodyModel noteData = new NoteBodyModel();
        noteData.setNote(testData.addRandomNote);
        NoteResponseModel noteResponseModel = step("Создание заметки", () ->
                given(defLogWithAuthSpec)
                        .body(noteData)

                        .when()
                        .post("/api/portfolioGroup/" + testData.specialPortfolioId + "/notes")

                        .then()
                        .spec(defRespLogSpec)
                        .statusCode(200)
                        .extract().as(NoteResponseModel.class));

        step("Check response", () ->
                assertThat(noteResponseModel.getNote()).isEqualTo(testData.addRandomNote));
    }

    @Test
    @Tag("Portfolio")
    @Owner("Kwlad1ck")
    @DisplayName("Редактирование рандомной заметки в портфолио")
    void successfulChangeNoteTest() {
        Response noteList = step("Полеченеие списка заметок портфолио", () ->
                given(defLogWithAuthSpec)
                        .queryParam("page", 0)
                        .queryParam("size", 15)

                        .when()
                        .get("/api/portfolioGroup/" + testData.specialPortfolioId + "/notes")

                        .then()
                        .spec(notesResponseSpec)
                        .statusCode(200)
                        .extract().response());

        JsonPath jsonPathForNoteList = noteList.jsonPath();
        List<Integer> portfolioNotesId = jsonPathForNoteList.getList("content.id");
        Integer randomNotesId = getRandomId(portfolioNotesId);

        NoteBodyModel noteData = new NoteBodyModel();
        noteData.setNote(testData.updateNotes);
        NoteResponseModel noteResponseModel = step("Редактирование заметки", () ->
                given(defLogWithAuthSpec)
                        .body(noteData)

                        .when()
                        .put("/api/portfolioGroup/" + testData.specialPortfolioId + "/notes/" + randomNotesId)

                        .then()
                        .spec(defRespLogSpec)
                        .statusCode(200)
                        .extract().as(NoteResponseModel.class));

        step("Check response", () ->
                assertThat(noteResponseModel.getNote()).isEqualTo(testData.updateNotes));
    }

    @Test
    @Tag("Portfolio")
    @Owner("Kwlad1ck")
    @DisplayName("Удаление заметки в портфолио")
    void successfulDeleteNoteTest() {
        Response noteList = step("Полеченеие списка заметок портфолио", () ->
                given(defLogWithAuthSpec)
                        .queryParam("page", 0)
                        .queryParam("size", 15)

                        .when()
                        .get("/api/portfolioGroup/" + testData.specialPortfolioId + "/notes")

                        .then()
                        .spec(defRespLogSpec)
                        .statusCode(200)
                        .extract().response());

        JsonPath jsonPathForNoteList = noteList.jsonPath();
        List<Integer> portfolioNotesId = jsonPathForNoteList.getList("content.id");
        Integer randomNotesId = getRandomId(portfolioNotesId);

        NoteResponseModel noteResponseModel = step("Удаление заметки из портфолио", () ->
                given(defLogWithAuthSpec)

                        .when()
                        .delete("/api/portfolioGroup/" + testData.specialPortfolioId
                                + "/notes/" + randomNotesId)

                        .then()
                        .spec(defRespLogSpec)
                        .statusCode(200)
                        .extract().as(NoteResponseModel.class));

        step("Check response", () ->
                assertThat(noteResponseModel.getStatus()).isEqualTo("OK"));
    }

    @Test
    @Tag("Transaction")
    @Owner("Kwlad1ck")
    @DisplayName("Получение цены монеты в рандомное время")
    void successfulTokenPriceAtTimeRequestTest() {
        PriceAtTimeResponseModel priceAtTimeResponseModel = step("Получение цены монеты", () ->
                given(defLogWithAuthSpec)
                        .queryParam("currencyId", testData.randomCurrencyId)
                        .queryParam("quoteSymbol", testData.randomQuoteSymbol)
                        .queryParam("time", testData.randomDataAtTime)

                        .when()
                        .get("/api/currencyHistorical/dataAtTime")

                        .then()
                        .spec(defRespLogSpec)
                        .statusCode(200)
                        .extract().as(PriceAtTimeResponseModel.class));

        step("Check response", () -> {
            assertThat(priceAtTimeResponseModel.getCurrencyId()).isEqualTo(testData.randomCurrencyId);
            assertThat(priceAtTimeResponseModel.getPrice()).isNotNull();
            assertThat(priceAtTimeResponseModel.getQuoteSymbol()).isEqualTo(testData.randomQuoteSymbol);
        });
    }

    @Test
    @Tag("Transaction")
    @Owner("Kwlad1ck")
    @DisplayName("Добавление транзакции в портфолио")
    void successfulAddTxnInPortfolioTest() {
        Response response = step("Получение списка портфолио аккаунта", () ->
                given(defLogWithAuthSpec)

                        .when()
                        .get("/api/portfolioGroup/short")

                        .then()
                        .spec(defRespLogSpec)
                        .statusCode(200)
                        .extract().response());

        JsonPath jsonPath = response.jsonPath();
        List<Integer> userPortfolioId = jsonPath.getList("portfolioGroups.id");
        Integer randomPortfolioId = getRandomValueExcluding(userPortfolioId, testData.excludePortfolio);

        PriceAtTimeResponseModel priceAtTimeResponseModel = step("Получение цены монеты", () ->
                given(defLogWithAuthSpec)
                        .queryParam("currencyId", testData.randomCurrencyId)
                        .queryParam("quoteSymbol", testData.randomQuoteSymbol)
                        .queryParam("time", testData.randomDataAtTime)

                        .when()
                        .get("/api/currencyHistorical/dataAtTime")

                        .then()
                        .spec(defRespLogSpec)
                        .statusCode(200)
                        .extract().as(PriceAtTimeResponseModel.class));

        TransactionBodyModel txnBodyData = new TransactionBodyModel();
        txnBodyData.setComment(testData.commentAddTnx);
        txnBodyData.setCurrencyId(testData.randomCurrencyId);
        txnBodyData.setFee(testData.randomFee);
        txnBodyData.setFeeType(testData.feeType);
        txnBodyData.setGroupId(randomPortfolioId);
        txnBodyData.setPrice(priceAtTimeResponseModel.getPrice());
        txnBodyData.setQuantity(testData.randomQuantity);
        txnBodyData.setQuoteCurrencyId(getCurrencyId(testData.randomQuoteSymbol));
        txnBodyData.setTransactionType("BUY");
        txnBodyData.setTxDate(testData.randomDataAtTime);

        TransactionResponseModel transactionResponseModel = step("Добавление BUY транзакции в портфолио", () ->
                given(defLogWithAuthSpec)
                        .body(txnBodyData)

                        .when()
                        .post("/api/transaction")

                        .then()
                        .spec(txnResponseSpec)
                        .statusCode(200)
                        .extract().as(TransactionResponseModel.class));

        step("Check response", () -> {
            assertThat(transactionResponseModel.getComment()).isEqualTo(testData.commentAddTnx);
            assertThat(transactionResponseModel.getCurrencyId()).isEqualTo(testData.randomCurrencyId);
            assertThat(transactionResponseModel.getFee()).isEqualTo(testData.randomFee);
            assertThat(transactionResponseModel.getFeeType()).isEqualTo(testData.feeType);
            assertThat(transactionResponseModel.getPriceInQuote()).isEqualTo(priceAtTimeResponseModel.getPrice());
            assertThat(transactionResponseModel.getQuantity()).isEqualTo(testData.randomQuantity);
            assertThat(transactionResponseModel.getQuoteCurrency().getId()).isEqualTo(getCurrencyId(testData.randomQuoteSymbol));
            assertThat(transactionResponseModel.getTransactionType()).isEqualTo("BUY");
            assertThat(transactionResponseModel.getTxDate()).isEqualTo(testData.randomDataAtTime);
        });
    }
}