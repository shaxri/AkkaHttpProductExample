CREATE TABLE products (
                          id SERIAL PRIMARY KEY,
                          item_name VARCHAR(255) NOT NULL,
                          quantity INT NOT NULL,
                          price DECIMAL(10, 2) NOT NULL
);

CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       username VARCHAR(255) NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       role VARCHAR(50) NOT NULL
);

CREATE TABLE user_products (
                               id SERIAL PRIMARY KEY,
                               user_id INT,
                               product_id INT,
                               FOREIGN KEY (user_id) REFERENCES users(id),
                               FOREIGN KEY (product_id) REFERENCES products(id)
);