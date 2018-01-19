-- CREATE TABLE location_info (
--   "id"       SERIAL PRIMARY KEY,
--   "fb_id" VARCHAR,
--   "city" VARCHAR NOT NULL,
--   "country" VARCHAR NOT NULL,
--   "latitude" VARCHAR NOT NULL,
--   "longitude" VARCHAR NOT NULL,
--   "street" VARCHAR NOT NULL,
--   "zip" VARCHAR NOT NULL,
--   "created_at" TIMESTAMP NOT NULL  );
CREATE TABLE page_info (
  "id"       SERIAL PRIMARY KEY,
  "name" VARCHAR NOT NULL,
  "phone" VARCHAR,
  "price_range" VARCHAR,
  "city" VARCHAR NOT NULL,
  "country" VARCHAR,
  "latitude" Float NOT NULL,
  "longitude" Float NOT NULL,
  "street" VARCHAR,
  "zip" VARCHAR,
  "created_at" TIMESTAMP NOT NULL);

-- ALTER TABLE page_info
--   ADD CONSTRAINT fk_location_id
-- FOREIGN KEY (location_id)
-- references location_info (id);
