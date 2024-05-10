package models.portfolio;

import lombok.Data;

@Data
public class OptionsPortfolioResponseModel {

    String chartTimeframe, color, portfolioTimeframe;

    Boolean includeInTotal, showChart, showHoldingsShareChart, showNotes, showSmallHoldings,
            showUpcomingEvents, topPerformance;
}
