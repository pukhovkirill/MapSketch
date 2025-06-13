DROP TABLE IF EXISTS geo_objects;

CREATE TABLE geo_objects (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    type VARCHAR(50) NOT NULL,
    coordinates TEXT NOT NULL
);
