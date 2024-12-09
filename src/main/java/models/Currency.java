package models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

@Data
@AllArgsConstructor
@JsonPropertyOrder({"id", "name", "code", "sign"})
public class Currency {
    private int id;
    @JsonProperty("name")
    private String fullName;
    private String code;
    private String sign;
}


