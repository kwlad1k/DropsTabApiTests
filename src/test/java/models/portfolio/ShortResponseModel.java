package models.portfolio;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class ShortResponseModel {

    String username, profileImage, btcPriceUsd, btcChange24h, ethPriceUsd, ethChange24h,
            btcDominance, globalInitialType;

    List<PortfolioGroup> portfolioGroups;

    @Data
    public static class PortfolioGroup {

        Integer id, userId, sortOrder, assetsCount, followersCount;

        String name, description, username, profileImage, shareType, portfolioSharingType,
                shareToken, sharingSlug;

        @JsonProperty("isSharingPublic")
        Boolean isSharingPublic;

        PortfolioTotal portfolioTotal;

        Options options;

        List<Asset> assets;

        Boolean following, needToIndex;

        Long updatedAt, createdAt;
    }

    @Data
    public static class PortfolioTotal {

        Map<String, String> totalCap, initialCap, profit, change, change24hPercent,
                change24hAbsolute;

        Map<String, Map<String, String>> netChangeAbsolute;

        Map<String, Map<String, Double>> netChangePercent, totalRealizedProfit, totalUnrealizedProfit;

        Boolean hideAmounts;
    }

    @Data
    public static class Options {

        String chartTimeframe, portfolioTimeframe, color;

        Boolean showChart, showSmallHoldings, showHoldingsShareChart, showUpcomingEvents,
                showNotes, topPerformance, includeInTotal;
    }

    @Data
    public static class Asset {

        Long id;

        String slug, name, symbol, image;

        Integer rank;

        Boolean custom;
    }
}
