package config;

import org.aeonbits.owner.Config;

@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources({
        "system:properties",
        "classpath:config/portfolio.properties"
})

public interface PortfolioConfig extends Config {

    @Key("id.portfolio")
    String portfolioId();
}
