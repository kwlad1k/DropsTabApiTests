package tests;

import com.github.javafaker.Faker;
import config.AuthConfig;
import config.PortfolioConfig;
import org.aeonbits.owner.ConfigFactory;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static utils.RandomUtils.getRandomTimestamp;

public class TestDataAPI {
    Faker faker = new Faker();
    Random random = new Random();
    ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Europe/Moscow"));
    DateTimeFormatter formatData = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss zzz");

    AuthConfig authConfig = ConfigFactory.create(AuthConfig.class);

    PortfolioConfig portfolioConfig = ConfigFactory.create(PortfolioConfig.class);

    public String specialPortfolioId = portfolioConfig.portfolioId(),
            authUserEmail = authConfig.userEmail(),
            authUserName = authConfig.userName(),
            authUserPassword = authConfig.password(),
            authorizationToken = authConfig.userToken(),
            randomEmail = faker.internet().emailAddress(),
            randomPassword = faker.internet().password(),
            randomUserName = faker.name().username(),
            randomColor = faker.options().option("CYAN", "INDIGO", "PURPLE", "ORANGE", "BLUE", "GREEN"),
            randomAddDescription = now.format(formatData) + " Added " + faker.lorem().paragraph(),
            randomChangeDescription = now.format(formatData) + " Changed " + faker.lorem().paragraph(),
            randomPortfolioName = faker.lorem().word() + " Portfolio",
            randomDuplicatePortfolioName = faker.lorem().word() + " Duplicate",
            randomWrongUserName = faker.regexify("[A-Za-z0-9]{33}"),
            randomSharingTypePortfolio = faker.options().option("NONE", "ALL", "ALL_HIDE_AMOUNTS"),
            randomSharingSlug = faker.regexify("[A-Za-z0-9]{5}"),
            invalidSharingSlug = faker.regexify("[A-Za-z0-9]{33}"),
            cyrillicSharingSlug = faker.regexify("[А-Яа-я0-9]{10}"),
            forbiddenWordsForSharingSlug = faker.options().option("suka", "pidor", "xyi"),
            randomChartTimeFrame = faker.options().option("1D", "1W", "1M", "3M", "6M", "1Y", "YTD"),
            randomPortfolioTimeframe = faker.options().option("1D", "1W", "1M", "3M", "6M", "1Y", "YTD"),
            addRandomNote = "Added note at " + now.format(formatData) + " " + faker.lorem().paragraph(),
            updateNotes = "Updated note at " + now.format(formatData),
            feeType = "PERCENT",
            randomQuoteSymbol = faker.options().option("USD", "USDT", "USDC", "USDD", "FDUSD", "TUSD",
                    "USDP", "DAI", "BTC", "ETH", "BNB", "SOL"),
            commentAddTnx = "Added at " + now.format(formatData);

    public Integer randomCurrencyId = faker.number().numberBetween(100, 200);

    public Double randomFee = faker.number().randomDouble(2, 0, 1),
            randomQuantity = faker.number().randomDouble(2, 1, 500) / 100.0;

    public Long randomDataAtTime = getRandomTimestamp("2020-01-01");

    public Boolean randomBoolean = random.nextBoolean(),
            rdmBlnInclTotal = random.nextBoolean(),
            rdmBlnShowChart = random.nextBoolean(),
            rdmBlnShowHoldingsShareChart = random.nextBoolean(),
            rdmBlnShowNotes = random.nextBoolean(),
            rdmBlnShowSmallHoldings = random.nextBoolean(),
            rdmBlnShowUpEvents = random.nextBoolean(),
            rdmBlnTopPerformance = random.nextBoolean();

    List<Integer> excludePortfolio = Arrays.asList(-1, 646205, 695134);

    public static final Map<String, Integer> currencyIdMap = new HashMap<>();

    static {
        currencyIdMap.put("USD", 2850);
        currencyIdMap.put("USDT", 32);
        currencyIdMap.put("USDC", 3134);
        currencyIdMap.put("USDD", 40622);
        currencyIdMap.put("FDUSD", 51574);
        currencyIdMap.put("TUSD", 2249);
        currencyIdMap.put("USDP", 4105);
        currencyIdMap.put("DAI", 14532);
        currencyIdMap.put("BTC", 1);
        currencyIdMap.put("ETH", 2);
        currencyIdMap.put("BNB", 36);
        currencyIdMap.put("SOL", 19067);
    }
}


