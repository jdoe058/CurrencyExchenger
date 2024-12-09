package dao;

import models.ExchangeRate;
import util.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExchangeRateDao implements Dao<Integer, ExchangeRate> {

    private static final ExchangeRateDao INSTANCE = new ExchangeRateDao();

    private static final String FIND_ALL_SQL = """
             SELECT id,
                base_currency_id,
                target_currency_id,
                rate
            FROM exchange_rates
            """;

    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + """
            WHERE id = ?
            """;

    private static final CurrencyDao currencyDao = CurrencyDao.getInstance();

    private ExchangeRateDao() {
    }

    public static ExchangeRateDao getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean delete(Integer id) {
        return false;
    }

    @Override
    public ExchangeRate save(ExchangeRate exchangeRate) {
        return null;
    }

    @Override
    public void update(ExchangeRate exchangeRate) {

    }

    public Optional<ExchangeRate> findById(Integer id) {
        try (Connection connection = ConnectionManager.get()) {
            return findById(id, connection);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<ExchangeRate> findById(Integer id, Connection connection) {
        try (//Connection connection = ConnectionManager.get();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            ExchangeRate rate = null;
            if (resultSet.next()) {
                rate = buildExchangeRate(resultSet);
            }
            return Optional.ofNullable(rate);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ExchangeRate> findAll() {
        List<ExchangeRate> rates = new ArrayList<>();
        try (Connection connection = ConnectionManager.get();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                rates.add(buildExchangeRate(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return rates;
    }

    private ExchangeRate buildExchangeRate(ResultSet resultSet) throws SQLException {
        Connection connection = resultSet.getStatement().getConnection();
        return new ExchangeRate(
                resultSet.getInt("id"),
                currencyDao.findById(resultSet.getInt("base_currency_id"), connection).orElse(null),
                currencyDao.findById(resultSet.getInt("target_currency_id"), connection).orElse(null),
                resultSet.getBigDecimal("rate")
        );
    }
}
