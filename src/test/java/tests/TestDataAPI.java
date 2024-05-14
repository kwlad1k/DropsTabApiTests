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

    public Integer randomCurrencyId = faker.options().option(1, 2, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,
            16, 17, 18, 22, 25, 28, 32, 34, 36, 37, 41, 46, 47, 49, 57, 64, 75, 76, 80, 94, 100, 103, 106, 126, 127,
            143, 148, 339, 1081, 1095, 1096, 1120, 1459, 1470, 1489, 1533, 1539, 1730, 1894, 1956, 1961, 1977, 2117,
            2195, 2209, 2249, 2349, 2579, 2594, 2667, 2703, 2822, 3134, 3183, 3258, 3325, 3345, 3349, 3365, 3443, 3516,
            3535, 3541, 3565, 3582, 3591, 3603, 3700, 3736, 3790, 3807,
            3823, 3831, 3841, 3857, 3882, 3893, 3910, 3942, 3965, 3988, 4045, 4046, 4091, 4112, 4116, 4131, 4139, 4156,
            14486, 14526, 14528, 14532, 14553, 14555, 14589, 14948, 15112, 18188, 18254, 18281, 18398, 18422, 19067,
            19146, 19279, 19296, 19366, 20191, 20204, 20325, 20534, 20611, 20620, 21101, 21110, 21171, 21397, 21527,
            21634, 21673, 21845, 21878, 21937, 21983, 22139, 22145, 22237, 22268, 22597, 22611, 22677, 22886, 22977,
            23105, 23298, 23556, 23558, 23580, 23860, 23897, 23899, 23931, 23993, 24136, 24164, 24657, 24716, 24821,
            24845, 24859, 24980, 25005, 25014, 25059, 25694, 25760, 25770, 26342, 26548, 26641, 26754, 26806, 27027,
            27036, 27128, 27599, 28198, 28909, 28939, 29071, 29848, 30039, 30293, 30599, 30615, 30665, 30777, 31180,
            31268, 31352, 31631, 31928, 32098, 32190, 32320, 33177, 33275, 33413, 33889, 34517, 34823, 34991, 35142,
            36452, 36826, 36849, 36903, 37194, 37411, 37478, 37733, 37738, 38382, 38930, 39126, 39316, 39480, 40622,
            40689, 41106, 41213, 41309, 41618, 43417, 43443, 43454, 43925, 44252, 44770, 46498, 46669, 47065, 47275,
            47382, 47418, 47455, 47778, 47824, 48387, 48401, 48854, 49060, 49369, 49629, 49759, 50002, 50371, 50961,
            51022, 51268, 51401, 51574, 51905, 51923, 51943, 52045, 52293, 52455, 52945, 53862, 53889, 54251, 54537,
            55018, 55230, 55384, 55683, 55761, 55926, 55975, 56044, 56528, 57579, 57683, 57883, 58069, 58512, 58678,
            58832, 59305, 59623, 60144, 60738, 60839, 60991, 61712, 61773, 62663, 63147, 63232, 63412, 64593, 65028,
            66187, 68384);

    public Double randomFee = faker.number().randomDouble(2, 0, 1),
            randomQuantity = faker.number().randomDouble(2, 1, 500) / 100.0;

    public Long randomDataAtTime = getRandomTimestamp("2020-01-01"),
            randomDataBtcDom = getRandomTimestamp("2013-01-01"),
            currentTime = System.currentTimeMillis();

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


