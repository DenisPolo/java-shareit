--DROP TABLE IF EXISTS PUBLIC.users CASCADE;
CREATE TABLE IF NOT EXISTS users (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  name VARCHAR(255) NOT NULL,
  email VARCHAR(512) NOT NULL,
  registration_date TIMESTAMP NOT NULL,
  CONSTRAINT pk_user PRIMARY KEY (id),
  CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

--DROP TABLE IF EXISTS PUBLIC.items CASCADE;
CREATE TABLE IF NOT EXISTS items (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  owner_id BIGINT NOT NULL,
  name VARCHAR(1000) NOT NULL,
  description VARCHAR(200),
  available BOOLEAN,
  creation_date TIMESTAMP NOT NULL,
  CONSTRAINT pk_item PRIMARY KEY (id),
  CONSTRAINT fk_items_to_users FOREIGN KEY(owner_id) REFERENCES users(id),
  UNIQUE(id)
);

--DROP TABLE IF EXISTS PUBLIC.bookings CASCADE;
CREATE TABLE IF NOT EXISTS bookings (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  start_time TIMESTAMP NOT NULL,
  end_time TIMESTAMP NOT NULL,
  booker_id BIGINT NOT NULL,
  item_id BIGINT NOT NULL,
  creation_date TIMESTAMP NOT NULL,
  status varchar(50),
  CONSTRAINT pk_booking PRIMARY KEY (id),
  CONSTRAINT fk_bookings_to_users FOREIGN KEY(booker_id) REFERENCES users(id),
  CONSTRAINT fk_bookings_to_items FOREIGN KEY(item_id) REFERENCES items(id),
  UNIQUE(id)
);

--DROP TABLE IF EXISTS PUBLIC.comments CASCADE;
CREATE TABLE IF NOT EXISTS comments (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  user_id BIGINT NOT NULL,
  item_id BIGINT NOT NULL,
  comment_text VARCHAR(300),
  creation_date TIMESTAMP NOT NULL,
  CONSTRAINT pk_comment PRIMARY KEY (id),
  CONSTRAINT fk_comments_to_user FOREIGN KEY(user_id) REFERENCES users(id),
  CONSTRAINT fk_comments_to_items FOREIGN KEY(item_id) REFERENCES items(id),
  UNIQUE(id)
);