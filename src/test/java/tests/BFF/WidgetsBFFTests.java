package tests.BFF;

import io.qameta.allure.Owner;
import models.widgets.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static spec.DefaultSpec.*;
import static spec.WidgetsSpec.*;

public class WidgetsBFFTests extends TestBaseBFF {

    @Test
    @Tag("Widgets")
    @Owner("Kwlad1ck")
    @DisplayName("Полученние данных виджета Fear/Greed Index")
    void successfulResponseFearGreedIndexRequestTest() {
        FearGreedIndexResponseModel fearGreedIndexResponseModel = step("Полученние данных Fear/Greed Index", () ->
                given(allLoggingSpec)
                        .queryParam("fields", "fearGreedIndexHistory")
                        .queryParam("fearGreedIndexTimeframe", "3M")

                        .when()
                        .get("/market-total-and-widgets-summary")

                        .then()
                        .spec(fearGreedIndexResponseSpec)
                        .statusCode(200)
                        .extract().as(FearGreedIndexResponseModel.class));

        step("Check response", () -> {
            assertThat(fearGreedIndexResponseModel.getOk()).isTrue();
            assertThat(fearGreedIndexResponseModel.getMessage()).isEqualTo("OK");
            assertThat(fearGreedIndexResponseModel.getData().getFearGreedIndexHistory().getTime().toArray(new Long[0]))
                    .isNotEmpty()
                    .allMatch(time -> time != null && time != 0L);
            assertThat(fearGreedIndexResponseModel.getData().getFearGreedIndexHistory().getValue().toArray(new Integer[0]))
                    .isNotEmpty()
                    .allMatch(value -> value != null && value != 0);
            assertThat(fearGreedIndexResponseModel.getData().getFearGreedIndexHistory().getValueClassification().toArray(new String[0]))
                    .isNotEmpty();
        });
    }

    @Test
    @Tag("Widgets")
    @Owner("Kwlad1ck")
    @DisplayName("Полученние данных виджета SPX и Gold")
    void successfulResponseSPXAndGoldRequestTest() {
        SpxAndGoldResponseModel spxAndGoldResponseModel = step("Полученние данных Fear/Greed Index", () ->
                given(defLogSpec)
                        .queryParam("fields", "stockRealtimeSPX,stockRealtimeGOLD")

                        .when()
                        .get("/market-total-and-widgets-summary")

                        .then()
                        .spec(spxAndGoldResponseSpec)
                        .statusCode(200)
                        .extract().as(SpxAndGoldResponseModel.class));

        step("Check response", () -> {
            assertThat(spxAndGoldResponseModel.getOk()).isTrue();
            assertThat(spxAndGoldResponseModel.getMessage()).isEqualTo("OK");
            assertThat(spxAndGoldResponseModel.getData().getStockRealtimeGOLD().getChange())
                    .isNotNull()
                    .isNotEqualTo(0);
            assertThat(spxAndGoldResponseModel.getData().getStockRealtimeGOLD()
                    .getStockMarket().getCurrencyType()).isEqualTo("GOLD");
            assertThat(spxAndGoldResponseModel.getData().getStockRealtimeGOLD().getStockMarket().getPrice())
                    .isNotNull()
                    .isNotEqualTo(0);
            assertThat(spxAndGoldResponseModel.getData().getStockRealtimeSPX().getChange())
                    .isNotNull()
                    .isNotEqualTo(0);
            assertThat(spxAndGoldResponseModel.getData().getStockRealtimeSPX()
                    .getStockMarket().getCurrencyType()).isEqualTo("SPX");
            assertThat(spxAndGoldResponseModel.getData().getStockRealtimeSPX().getStockMarket().getPrice())
                    .isNotNull()
                    .isNotEqualTo(0);
        });
    }
}
