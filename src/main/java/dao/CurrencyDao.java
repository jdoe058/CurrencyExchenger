package dao;

import exeptions.ErrorException;
import models.Currency;
import util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CurrencyDao {

    private static final CurrencyDao INSTANCE = new CurrencyDao();

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

//    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + """
//            WHERE id = ?
//            """;
//
//    private static final String DELETE_SQL = """
//            DELETE FROM currencies
//            WHERE id = ?
//            """;
//
//    private static final String SAVE_SQL = """
//            INSERT INTO currencies (full_name, code, sign)
//            VALUES (?, ?, ?)
//            """;
//
//    private static final String UPDATE_SQL = """
//            UPDATE currencies
//            SET full_name = ?,
//                code = ?,
//                sign = ?
//            where id = ?
//            """;

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
            throw new ErrorException();
        }
    }

    //todo CurrencyFilterDto
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
            throw new ErrorException();
        }
    }

//    public Optional<Currency> findById(Integer id) throws SQLException {
//        try (Connection connection = ConnectionManager.get()) {
//            return findById(id, connection);
//        }
//    }
//
//    public Optional<Currency> findById(Integer id, Connection connection) throws SQLException {
//        try (PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
//            preparedStatement.setInt(1, id);
//            ResultSet resultSet = preparedStatement.executeQuery();
//            Currency currency = null;
//            if (resultSet.next()) {
//                currency = buildCurrency(resultSet);
//            }
//            return Optional.ofNullable(currency);
//        }
//    }
//
//    public void update(Currency currency) throws SQLException {
//        try (Connection connection = ConnectionManager.get();
//             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL)) {
//            preparedStatement.setString(1, currency.getFullName());
//            preparedStatement.setString(2, currency.getCode());
//            preparedStatement.setString(3, currency.getSign());
//            preparedStatement.setInt(4, currency.getId());
//            preparedStatement.executeUpdate();
//        }
//    }
//
//    public Currency save(Currency currency) throws SQLException {
//        try (Connection connection = ConnectionManager.get();
//             PreparedStatement preparedStatement = connection.prepareStatement(
//                     SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
//            preparedStatement.setString(1, currency.getFullName());
//            preparedStatement.setString(2, currency.getCode());
//            preparedStatement.setString(3, currency.getSign());
//            preparedStatement.executeUpdate();
//            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
//            if (generatedKeys.next()) {
//                currency.setId(generatedKeys.getInt("id"));
//            }
//            return currency;
//        }
//    }
//
//    public boolean delete(Integer id) throws SQLException {
//        try (Connection connection = ConnectionManager.get();
//             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_SQL)) {
//            preparedStatement.setInt(1, id);
//            return preparedStatement.executeUpdate() > 0;
//        }
//    }

    public static CurrencyDao getInstance() {
        return INSTANCE;
    }

    private Currency buildCurrency(ResultSet resultSet) throws SQLException {
        return new Currency(
                resultSet.getInt("id"),
                resultSet.getString("full_name"),
                resultSet.getString("code"),
                resultSet.getString("sign")
        );
    }
}
