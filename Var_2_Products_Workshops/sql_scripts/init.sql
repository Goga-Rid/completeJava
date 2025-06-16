-- Создание базы данных
CREATE DATABASE мебельное_производство;

-- Подключение к базе данных
\c мебельное_производство

-- Создание таблицы Типы материалов
CREATE TABLE material_types (
    material_type_id SERIAL PRIMARY KEY,
    material_name VARCHAR(100) NOT NULL UNIQUE,
    raw_material_loss_percentage DECIMAL(5,4) NOT NULL
);

-- Создание таблицы Типы продукции
CREATE TABLE product_types (
    product_type_id SERIAL PRIMARY KEY,
    product_type_name VARCHAR(50) NOT NULL UNIQUE,
    product_type_coefficient DECIMAL(3,1) NOT NULL
);

-- Создание таблицы Цеха
CREATE TABLE workshops (
    workshop_id SERIAL PRIMARY KEY,
    workshop_name VARCHAR(100) NOT NULL UNIQUE,
    workshop_type VARCHAR(50) NOT NULL,
    workers_count INTEGER NOT NULL
);

-- Создание таблицы Продукция
CREATE TABLE products (
    product_id SERIAL PRIMARY KEY,
    product_type_id INTEGER REFERENCES product_types(product_type_id),
    product_name VARCHAR(100) NOT NULL UNIQUE,
    article_number INTEGER NOT NULL UNIQUE,
    min_partner_price DECIMAL(10,2) NOT NULL,
    main_material_id INTEGER REFERENCES material_types(material_type_id)
);

-- Создание таблицы Производственные процессы
CREATE TABLE production_processes (
    process_id SERIAL PRIMARY KEY,
    product_id INTEGER REFERENCES products(product_id),
    workshop_id INTEGER REFERENCES workshops(workshop_id),
    production_time_hours DECIMAL(3,1) NOT NULL
);

-- Импорт данных в таблицу material_types
\COPY material_types(material_name, raw_material_loss_percentage) FROM '/home/goga_rid/Загрузки/data_demotest/Material_type_import.csv' WITH (FORMAT csv, HEADER true, DELIMITER '|');

-- Импорт данных в таблицу product_types
\COPY product_types(product_type_name, product_type_coefficient) FROM '/home/goga_rid/Загрузки/data_demotest/Product_type_import.csv' WITH (FORMAT csv, HEADER true, DELIMITER '|');

-- Импорт данных в таблицу workshops
\COPY workshops(workshop_name, workshop_type, workers_count) FROM '/home/goga_rid/Загрузки/data_demotest/Workshops_import.csv' WITH (FORMAT csv, HEADER true, DELIMITER '|');

-- Импорт данных в таблицу products

-- Создаем таблицу-приемник
CREATE TABLE products_import (
    product_type_name TEXT,
    product_name TEXT,
    article_number INTEGER,
    min_partner_price DECIMAL(10,2),
    main_material_name TEXT
);

-- Импортируем данные
\COPY products_import FROM '/home/goga_rid/Загрузки/data_demotest/Products_import.csv' WITH (FORMAT csv, HEADER true, DELIMITER '|');

-- Переносим данные
INSERT INTO products (product_type_id, product_name, article_number, min_partner_price, main_material_id)
SELECT 
    pt.product_type_id,
    pi.product_name,
    pi.article_number,
    pi.min_partner_price,
    mt.material_type_id
FROM 
    products_import pi
JOIN 
    product_types pt ON pi.product_type_name = pt.product_type_name
JOIN 
    material_types mt ON pi.main_material_name = mt.material_name;

-- Удаляем таблицу-приемник
DROP TABLE products_import;

-- Импорт данных в таблицу production_processes

-- Создаем временную таблицу для импорта всех данных из CSV
CREATE TABLE production_processes_import (
    product_name TEXT,
    workshop_name TEXT,
    production_time_hours DECIMAL(3,1)
);

-- Импортируем данные во временную таблицу
\COPY production_processes_import FROM '/home/goga_rid/Загрузки/data_demotest/Product_workshops_import.csv' WITH (FORMAT csv, HEADER true, DELIMITER '|');

-- Вставляем данные в основную таблицу production_processes с подстановкой ID
INSERT INTO production_processes (product_id, workshop_id, production_time_hours)
SELECT 
    p.product_id,
    w.workshop_id,
    ppi.production_time_hours
FROM 
    production_processes_import ppi
JOIN 
    products p ON ppi.product_name = p.product_name
JOIN 
    workshops w ON ppi.workshop_name = w.workshop_name;

-- Удаляем временную таблицу
DROP TABLE production_processes_import;