package models.portfolio;

import lombok.Data;

@Data
public class OptionsPortfolioBodyModel {

    String chartTimeframe, color, portfolioTimeframe;

    Integer groupId;

    Boolean includeInTotal, showChart, showHoldingsShareChart, showNotes, showSmallHoldings,
            showUpcomingEvents, topPerformance;
}
