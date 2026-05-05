package models.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class CurrentResponseModel {

    Boolean success, enabled;

    String data, code, message, email, username, role, profileImage, selectedPresetGuid,
            shareType, telegramVerificationCode, telegramDisplayName;

    Integer id;

    Options options;

    @JsonProperty("isBannedForSharePreset")
    Boolean isBannedForSharePreset;

    Boolean telegramLinked;

    List<Integer> presetOrderById;

    List<Object> authorities;

    Object sharingUsers;

    @Data
    public static class Options {

        @JsonProperty("globalInitialType")
        String globalInitialType;

        String timeframe;

        Boolean showHeader, onlyVerifiedVolumes;
    }
}
