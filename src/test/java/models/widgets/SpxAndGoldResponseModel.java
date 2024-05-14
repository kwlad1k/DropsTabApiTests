package models.widgets;

import lombok.Data;

@Data
public class SpxAndGoldResponseModel {

        Boolean ok;
        String message;
        StockData data;

    @Data
    public static class StockData {
        StockRealtime stockRealtimeSPX;
        StockRealtime stockRealtimeGOLD;
    }

    @Data
    public static class StockRealtime {
        Boolean marketStatus;
        StockMarket stockMarket;
        Double change;
    }

    @Data
    public static class StockMarket {
        Double price;
        String currencyType;
        Long updateTime;
    }
}
