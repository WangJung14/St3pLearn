-- Active: 1782630393903@@127.0.0.1@5432@st3plearn
-- Master psql script.
-- Run from BE/docs/Query with:
-- psql -U admin -h localhost -d postgres -f seed_all_databases.sql
--
-- The script assumes identity_db, catalog_db, and learning_db already exist
-- and that the services have already created their tables via Hibernate.

\set ON_ERROR_STOP on

\connect identity_db
\i seed_identity_db.sql

\connect catalog_db
\i seed_catalog_db.sql

\connect learning_db
\i seed_learning_db.sql
