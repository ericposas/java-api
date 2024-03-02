CREATE TABLE IF NOT EXISTS USERS (
    id SERIAL PRIMARY KEY,
    firstname VARCHAR(255) NOT NULL,
    middlename VARCHAR(255),
    lastname VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS ADDRESSES (
    id SERIAL PRIMARY KEY,
    line1 VARCHAR(255) NOT NULL,
    line2 VARCHAR(255),
    city VARCHAR(255) NOT NULL,
    postalcode VARCHAR(255) NOT NULL,
    stateprovince VARCHAR(255) NOT NULL,
    countryid VARCHAR(255) NOT NULL,
    UNIQUE(line1)
);

CREATE TABLE IF NOT EXISTS EMAILS (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    UNIQUE(email)
);

CREATE TABLE IF NOT EXISTS PHONENUMBERS (
    id SERIAL PRIMARY KEY,
    phonenumber VARCHAR(255) NOT NULL,
    phonetype VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS USERSPHONENUMBERS (
    user_id INT NOT NULL,
    phonenumber_id INT NOT NULL,
    isprimary BOOLEAN,
    FOREIGN KEY (user_id) REFERENCES USERS(id) ON DELETE CASCADE,
    FOREIGN KEY (phonenumber_id) REFERENCES PHONENUMBERS(id) ON DELETE CASCADE,
    UNIQUE(phonenumber_id),
    PRIMARY KEY(user_id, phonenumber_id)
);

CREATE TABLE IF NOT EXISTS USERSADDRESSES (
    user_id INT NOT NULL,
    address_id INT NOT NULL,
    isprimary BOOLEAN,
    FOREIGN KEY (user_id) REFERENCES USERS(id) ON DELETE CASCADE,
    FOREIGN KEY (address_id) REFERENCES ADDRESSES(id) ON DELETE CASCADE,
    UNIQUE(address_id),
    PRIMARY KEY(user_id, address_id)
);

CREATE TABLE IF NOT EXISTS USERSEMAILS(
    user_id INT NOT NULL,
    email_id INT NOT NULL,
    isprimary BOOLEAN,
    FOREIGN KEY (user_id) REFERENCES USERS(id) ON DELETE CASCADE,
    FOREIGN KEY (email_id) REFERENCES EMAILS(id) ON DELETE CASCADE,
    UNIQUE(email_id),
    PRIMARY KEY(user_id, email_id)
);

-- Users indexes
CREATE INDEX IF NOT EXISTS firstname_search ON USERS(firstname);

CREATE INDEX IF NOT EXISTS lastname_search ON USERS(lastname);

CREATE INDEX IF NOT EXISTS lowercase_firstname_search ON USERS(lower(firstname));

CREATE INDEX IF NOT EXISTS lowercase_lastname_search ON USERS(lower(lastname));

-- Emails indexes
CREATE INDEX IF NOT EXISTS email_search ON EMAILS(email);

-- Addresses indexes
CREATE INDEX IF NOT EXISTS address_line1_search ON ADDRESSES(line1);

CREATE INDEX IF NOT EXISTS address_city_search ON ADDRESSES(city);

CREATE INDEX IF NOT EXISTS address_zip_search ON ADDRESSES(postalcode);

CREATE INDEX IF NOT EXISTS address_state_search ON ADDRESSES(stateprovince);