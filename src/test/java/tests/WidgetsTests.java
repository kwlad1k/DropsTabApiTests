package tests;

import io.qameta.allure.Owner;
import models.widgets.BtcDominationResponseModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static spec.DefaultSpec.*;
import static spec.WidgetsSpec.*;

@DisplayName("Виджеты")
public class WidgetsTests extends TestBaseAPI {

    TestDataAPI testData = new TestDataAPI();

    @Test
    @Tag("Widgets")
    @Owner("Kwlad1ck")
    @DisplayName("Полученние данных BTC Dominance с текущего времени до рандомного промежутка")
    void successfulResponseBtcDominanceRequestTest() {
        BtcDominationResponseModel btcDominationResponseModel = step("Получение данных BTC доминации", () ->
                given(defLogSpec)
                        .queryParam("before", testData.currentTime)
                        .queryParam("after", testData.randomDataBtcDom)

                        .when()
                        .get("/api/marketTotal/btcDomination/history")

                        .then()
                        .spec(btcDominationResponseSpec)
                        .statusCode(200)
                        .extract().as(BtcDominationResponseModel.class));

        step("Check response", () -> {
            assertThat(btcDominationResponseModel.getChange())
                    .isNotNull()
                    .isNotEqualTo(0);
            assertThat(btcDominationResponseModel.getDominationPercent().toArray(new Double[0]))
                    .isNotEmpty()
                    .allMatch(dominationPercent -> dominationPercent != null && dominationPercent != 0);
            assertThat(btcDominationResponseModel.getTime().toArray(new Long[0]))
                    .isNotEmpty()
                    .allMatch(time -> time != null && time != 0L);
        });
    }
}
