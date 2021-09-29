--------------------------------------------------------------
-- Filename:  V1.3__reports.sql
--------------------------------------------------------------

-- Create this table:  reports
drop table if exists reports;
create table reports
(
    id                integer      not null,
    version           integer      not null,
    name              varchar(256) not null,
    priority          integer      null,
    start_date        timestamp    null,
    end_date          timestamp    null,
    primary key (id),
    constraint lookup_priority foreign key(priority) references  lookup(id)
);
comment on table reports      is 'This table holds all of the report metadata.';
comment on column reports.id   is 'Uniquely identifies this report';
