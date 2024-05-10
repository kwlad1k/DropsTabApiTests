package models.portfolio;


import lombok.Data;

@Data
public class TransactionBodyModel {

    Integer currencyId, groupId, quoteCurrencyId, relatedCurrencyId;

    Double price, feeCurrencyId, fee, quantity;

    Long txDate;

    String feeType, transactionType, comment;
}
