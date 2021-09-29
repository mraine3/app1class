--------------------------------------------------------------
-- Filename:  V1.4__jobs.sql
--------------------------------------------------------------


-- Create the jobs table
create table jobs (
      id                    integer PRIMARY KEY,
      state                 integer NOT NULL,
      progress_as_percent   integer,
      submitter_username    varchar(100),
      submitter_date        timestamp default now(),
      original_filename     varchar(100),
      user_message          varchar(2000)
);
