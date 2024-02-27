create table IF NOT EXISTS USERS (
    id SERIAL PRIMARY KEY,
    firstname varchar(255),
    lastname varchar(255)
);

create table IF NOT EXISTS ADDRESSES (
    id SERIAL PRIMARY KEY,
    line1 varchar(255),
    line2 varchar(255),
    city varchar(255),
    postalcode varchar(255),
    stateprovince varchar(255),
    countryid varchar(255),
    UNIQUE(line1)
);

create table IF NOT EXISTS USERSADDRESSES (
    user_id int,
    address_id int,
    isprimary BOOLEAN,
    FOREIGN KEY (user_id) REFERENCES USERS(id),
    FOREIGN KEY (address_id) REFERENCES ADDRESSES(id),
    UNIQUE(address_id)
);