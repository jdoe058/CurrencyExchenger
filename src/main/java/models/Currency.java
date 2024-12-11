package models;

import lombok.Builder;
import lombok.Value;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.ResultSet;
import java.sql.SQLException;

@Value
@Builder
public class Currency {
    int id;
    @JsonProperty("name")
    String fullName;
    CurrencyCode code;
    String sign;

    public String getCode() {
        return code.getCode();
    }

    public static Currency fromResultSetWithPrefix (ResultSet resultSet, String prefix) throws SQLException {
        return Currency.builder()
                .id(resultSet.getInt(prefix + "id"))
                .fullName(resultSet.getString(prefix + "full_name"))
                .code(CurrencyCode.of(resultSet.getString(prefix + "code")))
                .sign(resultSet.getString(prefix + "sign"))
                .build();
    }

    public Currency withId(int id) {
        return Currency.builder().id(id)
                .fullName(fullName).code(code).sign(sign).build();
    }
}


