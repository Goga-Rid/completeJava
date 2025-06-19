-- 1. Создаем таблицу product_types (до products)
CREATE TABLE product_types (
    product_type_id SERIAL PRIMARY KEY,
    type_name VARCHAR(100) NOT NULL,
    type_coefficient DECIMAL(5,2) NOT NULL
);

-- 2. Создаем таблицу material_types
CREATE TABLE material_types (
    material_type_id SERIAL PRIMARY KEY,
    type_name VARCHAR(100) NOT NULL,
    defect_percentage DECIMAL(5,2) NOT NULL
);

-- 3. Создаем таблицу partners
CREATE TABLE partners (
    partner_id SERIAL PRIMARY KEY,
    partner_type VARCHAR(50) NOT NULL,
    partner_name VARCHAR(100) NOT NULL,
    director VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    legal_address VARCHAR(200) NOT NULL,
    inn VARCHAR(12) NOT NULL,
    rating INTEGER CHECK (rating BETWEEN 1 AND 10)
);

-- 4. Создаем таблицу products
CREATE TABLE products (
    product_id SERIAL PRIMARY KEY,
    product_type_id INTEGER NOT NULL REFERENCES product_types(product_type_id),
    product_name VARCHAR(200) NOT NULL,
    article VARCHAR(20) NOT NULL,
    min_partner_price DECIMAL(10,2) NOT NULL
);

-- 5. Создаем таблицу partner_requests
CREATE TABLE partner_requests (
    request_id SERIAL PRIMARY KEY,
    partner_id INTEGER REFERENCES partners(partner_id),
    product_id INTEGER REFERENCES products(product_id),
    material_type_id INTEGER REFERENCES material_types(material_type_id),
    request_date DATE DEFAULT CURRENT_DATE,
    quantity INTEGER NOT NULL,
    status VARCHAR(50) DEFAULT 'Новый'
);

-- =============================================
-- 2. Импортируем данные из CSV файлов
-- =============================================

-- 1. Импортируем типы продукции
\COPY product_types(type_name, type_coefficient) FROM '/home/goga_rid/Мусорка/data_student2/Product_type_import.csv' DELIMITER '|' CSV HEADER ENCODING 'UTF8';

-- 2. Импортируем материалы
\COPY material_types(type_name, defect_percentage) FROM '/home/goga_rid/Мусорка/data_student2/Material_type_import.csv' DELIMITER '|' CSV HEADER ENCODING 'UTF8';

-- 3. Импортируем партнеров
\COPY partners(partner_type, partner_name, director, email, phone, legal_address, inn, rating) FROM '/home/goga_rid/Мусорка/data_student2/Partners_import.csv' DELIMITER '|' CSV HEADER ENCODING 'UTF8';

-- 4. Создаем временную таблицу для импорта продуктов
CREATE TEMP TABLE temp_products (
    type_name VARCHAR(100),
    product_name VARCHAR(200),
    article VARCHAR(20),
    min_partner_price DECIMAL(10,2)
);

-- 5. Импортируем продукты во временную таблицу
\COPY temp_products FROM '/home/goga_rid/Мусорка/data_student2/Products_import.csv' DELIMITER '|' CSV HEADER ENCODING 'UTF8';

-- 6. Вставляем данные в основную таблицу products с привязкой к product_type_id
INSERT INTO products (product_type_id, product_name, article, min_partner_price)
SELECT pt.product_type_id, tp.product_name, tp.article, tp.min_partner_price
FROM temp_products tp
JOIN product_types pt ON tp.type_name = pt.type_name;

-- 7. Удаляем временную таблицу
DROP TABLE temp_products;

-- 8. Создаем временную таблицу для запросов партнеров
CREATE TABLE temp_partner_requests (
    product_name VARCHAR(200),
    partner_name VARCHAR(100),
    quantity INTEGER
);

-- 9. Импортируем запросы партнеров
\COPY temp_partner_requests FROM '/home/goga_rid/Мусорка/data_student2/Partner_products_request_import.csv' DELIMITER '|' CSV HEADER ENCODING 'UTF8';

-- 10. Вставляем запросы с правильными ID
INSERT INTO partner_requests (partner_id, product_id, material_type_id, quantity)
SELECT
    p.partner_id,
    pr.product_id,
    mt.material_type_id, -- Предположим, что мы хотим указать материал по умолчанию или через логику
    t.quantity
FROM
    temp_partner_requests t
JOIN
    partners p ON t.partner_name = p.partner_name
JOIN
    products pr ON t.product_name = pr.product_name
JOIN
    product_types pt ON pr.product_type_id = pt.product_type_id
JOIN
    material_types mt ON pt.type_name = mt.type_name; -- Пример связи, может быть другая

-- 11. Удаляем временную таблицу
DROP TABLE temp_partner_requests;
