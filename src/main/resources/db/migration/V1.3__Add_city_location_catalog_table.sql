CREATE TABLE city_location_catalog (
  "id"       SERIAL PRIMARY KEY,
  "name" VARCHAR NOT NULL,
  "alternate_names" VARCHAR,
  "latitude" Float NOT NULL,
  "longitude" Float NOT NULL,
  "created_at" TIMESTAMP NOT NULL);