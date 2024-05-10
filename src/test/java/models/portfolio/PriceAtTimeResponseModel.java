package models.portfolio;

import lombok.Data;

@Data
public class PriceAtTimeResponseModel {

    Integer currencyId;

    Double price;

    String quoteSymbol;

    Long time;
}
