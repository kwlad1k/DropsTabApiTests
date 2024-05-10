package models.portfolio;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PortfolioBodyModel {

    String color, description, name;

    Boolean includeInTotal;

    Integer groupId;
}
