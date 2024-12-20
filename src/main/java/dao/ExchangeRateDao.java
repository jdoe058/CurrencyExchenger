package dao;

import dto.ExchangeRateDto;
import exceptions.RestException;
import exceptions.RestNotFoundException;
import models.Currency;
import models.ExchangeRate;
import utilities.ConnectionManager;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class ExchangeRateDao {

    private static final ExchangeRateDao INSTANCE = new ExchangeRateDao();
    private static final CurrencyDao currencyDao = CurrencyDao.getInstance();

    private static final String FIND_ALL_SQL = """
            SELECT exchange_rates.id    AS id,
                base_currency_id        AS base_id,
                base.full_name          AS base_full_name,
                base.code               AS base_code,
                base.sign               AS base_sign,
                target_currency_id      AS target_id,
                target.full_name        AS target_full_name,
                target.code             AS target_code,
                target.sign             AS target_sign,
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

    private static final String INSERT_SQL = """
            INSERT INTO exchange_rates (base_currency_id, target_currency_id, rate)
            VALUES (?, ?, ?)
            """;

    private static final String UPDATE_SQL = """
            UPDATE exchange_rates
            SET rate = ?
            WHERE id = ?
            """;

    private ExchangeRateDao() {
    }

    public static ExchangeRateDao getInstance() {
        return INSTANCE;
    }

    public List<ExchangeRate> find() {
        return find(null);
    }

    public List<ExchangeRate> find(ExchangeRateDto filterDto) {
        try (Connection connection = ConnectionManager.get()) {
            ResultSet resultSet = find(filterDto, connection);
            List<ExchangeRate> rates = new ArrayList<>();
            while (resultSet.next()) {
                rates.add(buildExchangeRate(resultSet, null));
            }
            return rates;
        } catch (SQLException e) {
            throw new RestException();
        }
    }

    public ResultSet find(ExchangeRateDto filterDto, Connection connection) throws SQLException {
        String sql = filterDto != null ? FIND_BY_CODES_SQL : FIND_ALL_SQL;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            if (filterDto != null) {
                preparedStatement.setString(1, filterDto.baseCode().getCode());
                preparedStatement.setString(2, filterDto.targetCode().getCode());
            }
            return preparedStatement.executeQuery();
        }
    }

    public ExchangeRate update(ExchangeRateDto preparedRate) {
        try (Connection connection = ConnectionManager.get()) {
            ResultSet resultSet = find(preparedRate, connection);
            if (!resultSet.next()) {
                throw new RestNotFoundException("Одна (или обе) валюта из валютной пары не существует в БД");
            }
            ExchangeRate exchangeRate = buildExchangeRate(resultSet, preparedRate.amount());
            try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL)) {
                preparedStatement.setObject(1, preparedRate.amount());
                preparedStatement.setObject(2, exchangeRate.getId());
                preparedStatement.executeUpdate();
            }
            return exchangeRate;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ExchangeRate insert(ExchangeRate rate) {
        try (Connection connection = ConnectionManager.get();
             PreparedStatement preparedStatement =
                     connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            Currency baseCurrency = currencyDao.find(rate.getBaseCurrencyCode(), connection).getFirst();
            Currency targetCurrency = currencyDao.find(rate.getTargetCurrencyCode(), connection).getFirst();

            preparedStatement.setObject(1, baseCurrency.getId());
            preparedStatement.setObject(2, targetCurrency.getId());
            preparedStatement.setObject(3, rate.getRate());

            preparedStatement.executeUpdate();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                return ExchangeRate.builder().id(generatedKeys.getInt("id"))
                        .baseCurrency(baseCurrency)
                        .targetCurrency(targetCurrency)
                        .rate(rate.getRate()).build();
            }
            return null;
        } catch (NoSuchElementException e) {
            throw new RestNotFoundException("Одна (или обе) валюта из валютной пары не существует в БД");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static ExchangeRate buildExchangeRate(ResultSet resultSet, BigDecimal newRate) throws SQLException {
        return ExchangeRate.builder()
                .id(resultSet.getInt("id"))
                .baseCurrency(Currency.fromResultSetWithPrefix(resultSet, "base_"))
                .targetCurrency(Currency.fromResultSetWithPrefix(resultSet, "target_"))
                .rate(newRate == null ? resultSet.getBigDecimal("rate") : newRate)
                .build();
    }
}
