--------------------------------------------------------------
-- Filename:  V1.5__preferences.sql
--------------------------------------------------------------


-- Create the preferences table
create table preferences (
      id                    integer PRIMARY KEY,
      userid                integer NOT NULL,
      show_banner           boolean
);
