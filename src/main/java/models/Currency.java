package models;

import lombok.Builder;
import lombok.Value;
import com.fasterxml.jackson.annotation.JsonProperty;

@Value
@Builder
public class Currency {
    int id;
    @JsonProperty("name")
    String fullName;
    String code;
    String sign;
}


