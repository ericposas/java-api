CREATE TABLE IF NOT EXISTS USERS (
    id SERIAL PRIMARY KEY,
    firstname VARCHAR(255),
    lastname VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS ADDRESSES (
    id SERIAL PRIMARY KEY,
    line1 VARCHAR(255),
    line2 VARCHAR(255),
    city VARCHAR(255),
    postalcode VARCHAR(255),
    stateprovince VARCHAR(255),
    countryid VARCHAR(255),
    UNIQUE(line1)
);

CREATE TABLE IF NOT EXISTS USERSADDRESSES (
    user_id INT,
    address_id INT,
    isprimary BOOLEAN,
    FOREIGN KEY (user_id) REFERENCES USERS(id) ON DELETE CASCADE,
    FOREIGN KEY (address_id) REFERENCES ADDRESSES(id) ON DELETE CASCADE,
    UNIQUE(address_id),
    PRIMARY KEY(user_id, address_id)
);

CREATE INDEX IF NOT EXISTS firstname_search ON USERS(firstname);

CREATE INDEX IF NOT EXISTS lastname_search ON USERS(lastname);

CREATE INDEX IF NOT EXISTS lowercase_firstname_search ON USERS(lower(firstname));

CREATE INDEX IF NOT EXISTS lowercase_lastname_search ON USERS(lower(lastname));