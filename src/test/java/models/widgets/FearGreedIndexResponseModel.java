package models.widgets;

import lombok.Data;

import java.util.List;

@Data
public class FearGreedIndexResponseModel {

    Boolean ok;
    String message;
    DataContent data;

    @Data
    public static class DataContent {
        FearGreedIndexHistory fearGreedIndexHistory;
    }

    @Data
    public static class FearGreedIndexHistory {
        Long nextUpdate;
        List<Integer> value;
        List<String> valueClassification;
        List<Long> time;
    }
}
