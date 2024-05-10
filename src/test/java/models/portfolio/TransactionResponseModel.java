package models.portfolio;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponseModel {

    Integer currencyId, groupId, quoteCurrencyId, relatedCurrencyId, id, portfolioId;

    Double price, feeCurrencyId, fee, priceInQuote, priceUsd, profitLossAmount, feeQuantity, quantity;

    Long txDate, createdAt;

    String feeType, transactionType, comment;

    Boolean related, relationChangeable, partialMovement;

    Currency quoteCurrency;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Currency {

        Price icoPrice, price;

        IcoDate icoDate;

        Integer id;

        String image, name, symbol;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Price {

        String USD, BTC, ETH, BNB;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IcoDate {

        Long from, to;
    }
}
