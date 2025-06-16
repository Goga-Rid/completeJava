CREATE TABLE partners (
  partner_id SERIAL PRIMARY KEY,
  partner_type VARCHAR(50),
  partner_name VARCHAR(100) UNIQUE,  -- Делаем имя уникальным для связи с другими таблицами
  director VARCHAR(100),
  email VARCHAR(100),
  phone VARCHAR(20),
  legal_address VARCHAR(200),
  inn VARCHAR(12),
  rating INT,
  total_sales INT DEFAULT 0,
  discount INT DEFAULT 0
);

\COPY partners (partner_type, partner_name, director, email, phone, legal_address, inn, rating) FROM '/home/goga_rid/ГИА/2025gia/Ресурсы/Partners_import.csv' DELIMITER ',' CSV HEADER;

CREATE TABLE material_type (
  type VARCHAR(50) PRIMARY KEY,
  defect_percentage DECIMAL(10, 6)
);

\COPY material_type (type, defect_percentage) FROM '/home/goga_rid/ГИА/2025gia/Ресурсы/Material_type_import.csv' DELIMITER ',' CSV HEADER;

CREATE TABLE product_type (
  type VARCHAR(50) PRIMARY KEY,
  coefficient DECIMAL(10, 2)
);

\COPY product_type (type, coefficient) FROM '/home/goga_rid/ГИА/2025gia/Ресурсы/Product_type_import.csv' DELIMITER ';' CSV HEADER;

CREATE TABLE products (
  product_id SERIAL PRIMARY KEY,
  product_type VARCHAR(50) REFERENCES product_type(type),
  product_name VARCHAR(100),
  article INT,
  min_cost DECIMAL(10, 2)
);

ALTER TABLE products ADD CONSTRAINT unique_product_name UNIQUE (product_name);


\COPY products (product_type, product_name, article, min_cost) FROM '/home/goga_rid/ГИА/2025gia/Ресурсы/Products_import.csv' DELIMITER ',' CSV HEADER;


CREATE TABLE partner_products (
  transaction_id SERIAL PRIMARY KEY,
  product_name VARCHAR(100) REFERENCES products(product_name),
  partner_name VARCHAR(100) REFERENCES partners(partner_name),
  quantity INT,
  sale_date TIMESTAMP
);

\COPY partner_products (product_name, partner_name, quantity, sale_date) FROM '/home/goga_rid/ГИА/2025gia/Ресурсы/Partner_products_import.csv' DELIMITER ',' CSV HEADER;


