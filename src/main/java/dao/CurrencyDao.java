package dao;

import exceptions.CurrencyAlreadyExistsException;
import exceptions.RestException;
import models.Currency;
import utilities.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CurrencyDao {

    public static final String POSTGRES_UNIQUE_CONSTRAINT_VIOLATED = "23505";

    private static final String FIND_ALL_SQL = """
             SELECT id,
                full_name,
                code,
                sign
            FROM currencies
            """;
    private static final String FIND_BY_CODE_SQL = FIND_ALL_SQL + """
            WHERE code = ?
            """;
    private static final String SAVE_SQL = """
            INSERT INTO currencies (full_name, code, sign)
            VALUES (?, ?, ?)
            """;
    private static final CurrencyDao INSTANCE = new CurrencyDao();

    private CurrencyDao() {
    }

    public List<Currency> findAll() {
        List<Currency> currencies = new ArrayList<>();
        try (Connection connection = ConnectionManager.get();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                currencies.add(buildCurrency(resultSet));
            }
            return currencies;
        } catch (SQLException e) {
            throw new RestException();
        }
    }

    public Optional<Currency> findByCode(String code) {
        try (Connection connection = ConnectionManager.get();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_CODE_SQL)) {
            preparedStatement.setString(1, code);
            ResultSet resultSet = preparedStatement.executeQuery();
            Currency currency = null;
            if (resultSet.next()) {
                currency = buildCurrency(resultSet);
            }
            return Optional.ofNullable(currency);
        } catch (SQLException e) {
            throw new RestException();
        }
    }

    public Optional<Currency> save(Currency currency) {
        try (Connection connection = ConnectionManager.get();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, currency.getFullName());
            preparedStatement.setString(2, currency.getCode());
            preparedStatement.setString(3, currency.getSign());
            preparedStatement.executeUpdate();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                return Optional.of(Currency.builder()
                        .id(generatedKeys.getInt("id"))
                        .fullName(currency.getFullName())
                        .code(currency.getCode())
                        .sign(currency.getSign())
                        .build());
            }
            return Optional.empty();
        } catch (SQLException e) {
            if (e.getSQLState().equals(POSTGRES_UNIQUE_CONSTRAINT_VIOLATED)) {
                throw new CurrencyAlreadyExistsException();
            }
            throw new RestException();
        }
    }

    public static CurrencyDao getInstance() {
        return INSTANCE;
    }

    private Currency buildCurrency(ResultSet resultSet) throws SQLException {
        return Currency.builder()
                .id(resultSet.getInt("id"))
                .fullName(resultSet.getString("full_name"))
                .code(resultSet.getString("code"))
                .sign(resultSet.getString("sign"))
                .build();
    }
}
