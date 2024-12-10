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

    public Currency withId(int id) {
        return Currency.builder().id(id)
                .fullName(fullName).code(code).sign(sign).build();
    }
}


