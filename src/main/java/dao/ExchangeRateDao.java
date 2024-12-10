package dao;

import dto.ExchangeRateFilterDto;
import exceptions.RestException;
import exceptions.RestNotFoundException;
import models.Currency;
import models.ExchangeRate;
import utilities.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

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
            WHERE base.code = ?
                AND target.code = ?
            """;

    private static final String INSERT_SQL = """
            INSERT INTO exchange_rates (base_currency_id, target_currency_id, rate)
            VALUES (?, ?, ?)
            """;

    private ExchangeRateDao() {
    }

    public static ExchangeRateDao getInstance() {
        return INSTANCE;
    }

    //todo ExchangeRateFilterDto
    public Optional<ExchangeRate> findByCodes(ExchangeRateFilterDto filterDto) {
        try (Connection connection = ConnectionManager.get();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_CODES_SQL)) {
            preparedStatement.setString(1, filterDto.baseCode());
            preparedStatement.setString(2, filterDto.targetCode());
            ResultSet resultSet = preparedStatement.executeQuery();
            ExchangeRate rate = null;
            if (resultSet.next()) {
                rate = buildExchangeRate(resultSet);
            }
            return Optional.ofNullable(rate);
        } catch (SQLException e) {
            throw new RestException();
        }
    }

    public List<ExchangeRate> findAll() {
        List<ExchangeRate> rates = new ArrayList<>();
        try (Connection connection = ConnectionManager.get();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                rates.add(buildExchangeRate(resultSet));
            }
            return rates;
        } catch (SQLException e) {
            throw new RestException();
        }
    }

    public Optional<ExchangeRate> save(ExchangeRate rate) {
        try (Connection connection = ConnectionManager.get();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            Currency baseCurrency = currencyDao.find(rate.getBaseCurrency().getCode(), connection).getFirst();
            Currency targetCurrency = currencyDao.find(rate.getTargetCurrency().getCode(), connection).getFirst();

            preparedStatement.setObject(1, baseCurrency.getId());
            preparedStatement.setObject(2, targetCurrency.getId());
            preparedStatement.setObject(3, rate.getRate());
            preparedStatement.executeUpdate();

            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
               return Optional.of(ExchangeRate.builder().id(generatedKeys.getInt("id"))
                       .baseCurrency(baseCurrency).targetCurrency(targetCurrency).rate(rate.getRate()).build());
            }
            return Optional.empty();

        } catch (NoSuchElementException e) {
            throw new RestNotFoundException("Одна (или обе) валюта из валютной пары не существует в БД");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private ExchangeRate buildExchangeRate(ResultSet resultSet) throws SQLException {
        return ExchangeRate.builder()
                .id(resultSet.getInt("id"))
                .baseCurrency(Currency.fromResultSetWithPrefix(resultSet, "base_"))
                .targetCurrency(Currency.fromResultSetWithPrefix(resultSet, "target_"))
                .rate(resultSet.getBigDecimal("rate"))
                .build();
    }
}
