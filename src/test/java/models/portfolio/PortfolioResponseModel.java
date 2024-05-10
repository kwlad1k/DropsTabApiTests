package models.portfolio;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PortfolioResponseModel {

    Integer id, sortOrder, followersCount;

    String username, profileImage, name, description, shareType, portfolioSharingType,
            sharingSlug, shareToken;

    @JsonProperty("isSharingPublic")
    Boolean isSharingPublic;

    Options options;

    PortfolioTotal portfolioTotal;

    Portfolio[] portfolios;

    Object following;

    Object needToIndex;

    Long updatedAt, createdAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Options {

        String chartTimeframe, portfolioTimeframe, color;

        Boolean showChart, showSmallHoldings, showHoldingsShareChart, showUpcomingEvents,
                showNotes, topPerformance, includeInTotal;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PortfolioTotal {

        Map<String, String> totalCap, initialCap, profit, change, change24hPercent,
                change24hAbsolute;

        Map<String, Map<String, Integer>> netChangeAbsolute;

        Map<String, Map<String, Double>> netChangePercent;

        Boolean hideAmounts;

        Map<String, Integer> totalRealizedProfit, totalUnrealizedProfit;
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Portfolio {

        Integer id;

        String name;
    }
}

