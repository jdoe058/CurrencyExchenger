package dao;

import static utilities.ConnectionManager.POSTGRES_UNIQUE_CONSTRAINT_VIOLATED;

import models.Currency;
import exceptions.RestException;
import exceptions.RestConflictException;
import utilities.ConnectionManager;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import java.util.StringJoiner;

public class CurrencyDao {

    public static final String CONFLICT_MESSAGE = "Валюта с таким кодом уже существует";

    private static final String FIND_ALL_SQL = """
            SELECT id,
                full_name,
                code,
                sign
            FROM currencies
            """;

    private static final String SAVE_SQL = """
            INSERT INTO currencies (full_name, code, sign)
            VALUES (?, ?, ?)
            """;

    private static final CurrencyDao INSTANCE = new CurrencyDao();

    private CurrencyDao() {
    }

    public static CurrencyDao getInstance() {
        return INSTANCE;
    }

    public List<Currency> find(List<String> codes) {
        try (Connection connection = ConnectionManager.get()) {
            return codes.isEmpty() ? findAll(connection) : find(codes, connection);
        } catch (SQLException e) {
            throw new RestException("Database find error: " + e.getMessage(), e);
        }
    }

    public List<Currency> find(String code, Connection connection) throws SQLException {
        return find(List.of(code), connection);
    }

    public List<Currency> find(List<String> codes, Connection connection) throws SQLException {
        List<Object> parameters = new ArrayList<>();
        StringBuilder sql = new StringBuilder(FIND_ALL_SQL);

        if (!codes.isEmpty()) {
            StringJoiner joiner = new StringJoiner(" OR ", " WHERE ", " ");
            for (String code : codes) {
                joiner.add("code = ?");
                parameters.add(code);
            }
            sql.append(joiner);
        }

        List<Currency> currencies = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < parameters.size(); i++) {
                preparedStatement.setObject(i + 1, parameters.get(i));
            }

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                currencies.add(Currency.fromResultSetWithPrefix(resultSet, ""));
            }
        }
        return currencies;
    }

    public List<Currency> save(Currency currency) {
        try (Connection connection = ConnectionManager.get();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, currency.getFullName());
            preparedStatement.setString(2, currency.getCode());
            preparedStatement.setString(3, currency.getSign());
            preparedStatement.executeUpdate();

            List<Currency> list = new ArrayList<>();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                list.add(currency.withId(generatedKeys.getInt("id")));
            }
            return list;
        } catch (SQLException e) {
            if (e.getSQLState().equals(POSTGRES_UNIQUE_CONSTRAINT_VIOLATED)) {
                throw new RestConflictException(CONFLICT_MESSAGE);
            }
            throw new RestException("Database create error: " + e.getMessage(), e);
        }
    }

    private List<Currency> findAll(Connection connection) throws SQLException {
        List<Currency> currencies = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_SQL);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                currencies.add(Currency.fromResultSetWithPrefix(resultSet, ""));
            }
        }
        return currencies;
    }
}
