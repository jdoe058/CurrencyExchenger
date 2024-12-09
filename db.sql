-- for pgAdmin
DROP TABLE exchange_rates;
DROP TABLE currencies;
--
CREATE TABLE currencies(
    id SERIAL PRIMARY KEY,
    full_name VARCHAR(128) NOT NULL,
    code VARCHAR(3) NOT NULL UNIQUE,
    sign VARCHAR(3) NOT NULL
);
-- from https://www.iban.com/currency-codes
INSERT INTO currencies (code, sign, full_name)
VALUES
    ('USD', '$', 'United States dollar'),
    ('EUR', '€', 'Euro'),
	('RUB', '₽', 'Russian Ruble');
--
CREATE TABLE exchange_rates(
	id SERIAL PRIMARY KEY,
	base_currency_id   INT NOT NULL REFERENCES currencies(id),
	target_currency_id INT NOT NULL REFERENCES currencies(id),
	rate DECIMAL(9,6)  NOT NULL,
	UNIQUE(base_currency_id, target_currency_id)
);
-- from https://www.cbr.ru/currency_base/daily/
INSERT INTO exchange_rates(base_currency_id, target_currency_id, rate)
VALUES
	(3, 1,  99.4215),
	(3, 2, 106.3040);
