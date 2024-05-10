package models.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class CurrentResponseModel {

    Boolean success, enabled;

    String data, code, message, email, username, role, profileImage, selectedPresetGuid,
            shareType;

    Integer id;

    Options options;

    @JsonProperty("isBannedForSharePreset")
    Boolean isBannedForSharePreset;

    List<Integer> presetOrderById;

    Object sharingUsers;

    @Data
    public static class Options {

        @JsonProperty("globalInitialType")
        String globalInitialType;

        String timeframe;

        Boolean showHeader, onlyVerifiedVolumes;
    }
}
