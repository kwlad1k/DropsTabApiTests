package models.widgets;

import lombok.Data;

import java.util.List;

@Data
public class BtcDominationResponseModel {

    Long change;
    List<Double> dominationPercent;
    List<Long> time;
}
