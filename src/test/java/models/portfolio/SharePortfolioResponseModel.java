package models.portfolio;

import lombok.Data;

import java.util.List;

@Data
public class SharePortfolioResponseModel {

    List<String> emails;

    String sharingSlug, token, type, data, code, message;

    Boolean isSharingPublic, success;
}
