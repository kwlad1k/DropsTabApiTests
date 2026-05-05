package models.portfolio;

import lombok.Data;

@Data
public class OptionsPortfolioResponseModel {

    String chartTimeframe, color, portfolioTimeframe, avatar;

    Boolean includeInTotal, showChart, showHoldingsShareChart, showNotes, showSmallHoldings,
            showUpcomingEvents, topPerformance;
}
