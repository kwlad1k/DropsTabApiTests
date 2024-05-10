package config;

import org.aeonbits.owner.Config;

@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources({
        "system:properties",
        "classpath:config/auth.properties"
})

public interface AuthConfig extends Config {

    @Key("user.email")
    String userEmail();

    @Key("user.name.dt")
    String userName();

    @Key("user.password")
    String password();

    @Key("user.token")
    String userToken();
}
