package dao;

import models.Currency;
import models.ExchangeRate;
import util.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExchangeRateDao {

    private static final ExchangeRateDao INSTANCE = new ExchangeRateDao();

    private static final String FIND_ALL_SQL = """
             SELECT exchange_rates.id AS id,
                base_currency_id,
                base.full_name AS base_full_name,
                base.code AS base_code,
                base.sign AS base_sign,
                target_currency_id,
                target.full_name AS target_full_name,
                target.code AS target_code,
                target.sign AS target_sign,
                rate
            FROM exchange_rates
            JOIN currencies AS base
                ON base_currency_id = base.id
            JOIN currencies AS target
                ON target_currency_id = target.id
            """;

    private static final String FIND_BY_CODES_SQL = FIND_ALL_SQL + """
            WHERE base.code = ? AND target.code = ?
            """;

    private ExchangeRateDao() {
    }

    //todo ExchangeRateFilterDto
    public Optional<ExchangeRate> findByCodes(String baseCode, String targetCode) throws SQLException {
        try (Connection connection = ConnectionManager.get();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_CODES_SQL)) {
            preparedStatement.setString(1, baseCode);
            preparedStatement.setString(2, targetCode);
            ResultSet resultSet = preparedStatement.executeQuery();
            ExchangeRate rate = null;
            if (resultSet.next()) {
                rate = buildExchangeRate(resultSet);
            }
            return Optional.ofNullable(rate);
        }
    }

    public List<ExchangeRate> findAll() throws SQLException {
        List<ExchangeRate> rates = new ArrayList<>();
        try (Connection connection = ConnectionManager.get();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                rates.add(buildExchangeRate(resultSet));
            }
            return rates;
        }
    }

    public static ExchangeRateDao getInstance() {
        return INSTANCE;
    }

    private ExchangeRate buildExchangeRate(ResultSet resultSet) throws SQLException {

        Currency baseCurrency = new Currency(
                resultSet.getInt("base_currency_id"),
                resultSet.getString("base_full_name"),
                resultSet.getString("base_code"),
                resultSet.getString("base_sign"));

        Currency targetCurrency = new Currency(
                resultSet.getInt("target_currency_id"),
                resultSet.getString("target_full_name"),
                resultSet.getString("target_code"),
                resultSet.getString("target_sign"));

        return new ExchangeRate(
                resultSet.getInt("id"),
                baseCurrency,
                targetCurrency,
                resultSet.getBigDecimal("rate")
        );
    }
}
