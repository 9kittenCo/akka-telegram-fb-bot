CREATE TABLE location_info (
  "id"       SERIAL PRIMARY KEY,
  "city" VARCHAR NOT NULL,
  "country" VARCHAR NOT NULL,
  "latitude" VARCHAR NOT NULL,
  "longitude" VARCHAR NOT NULL,
  "street" VARCHAR NOT NULL,
  "zip" VARCHAR NOT NULL,
  "created_at" TIMESTAMP NOT NULL  );

CREATE TABLE page_info (
  "id"       SERIAL PRIMARY KEY,
  "name" VARCHAR NOT NULL,
  "location_id" INTEGER,
  "phone" VARCHAR NOT NULL,
  "price_range" VARCHAR NOT NULL,
  "created_at" TIMESTAMP NOT NULL  );

ALTER TABLE page_info
  ADD CONSTRAINT fk_location_id
FOREIGN KEY (location_id)
references location_info (id);
