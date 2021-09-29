--------------------------------------------------------------------------------
-- Filename:  R__security.sql
--
-- NOTE:  This is a repeatable migration file because this data does not change
--        So, if anything changes in this file, this script is re-executed on startup
--------------------------------------------------------------------------------
drop table if exists roles_uicontrols;
drop table if exists uicontrols;
drop table if exists roles;


-- Create this table:  roles
create table roles (
                       id   integer     not null,
                       name varchar(50) not null,
                       primary key(id)
);

comment on table  roles       is 'This table holds all of the application roles used by the web app.';
comment on column roles.id   is 'This number uniquely identifies this role.';
comment on column roles.name is 'This identifies the name of the role.';



-- Create this table:  uicontrols
create table uicontrols (
                            id   integer     not null,
                            name varchar(50) not null,
                            primary key(id)
);

comment on table  uicontrols       is 'This table holds all of the application roles used by the web app.';
comment on column uicontrols.id   is 'This number uniquely identifies this UI feature.';
comment on column uicontrols.name is 'This identifies the name of the UI feature.';


-- Create this table:  roles_uicontrols
create table roles_uicontrols (
                                  role_id      integer not null,
                                  uicontrol_id integer not null
);
comment on table  roles_uicontrols   is 'This table holds the relationships between the roles and uicontrols tables.';


--
-- Define the security roles
--
insert into roles(id, name) values (1, 'ADMIN');
insert into roles(id, name) values( 2, 'READER');


--
-- Add the uicontrols records
-- ASSUMPTION:  These routes match your routes in constants.ts
--
insert into uicontrols(id, name) values(1001, 'page/viewReports');
insert into uicontrols(id, name) values(1002, 'page/reports/add');
insert into uicontrols(id, name) values(1003, 'page/reports/add2');
insert into uicontrols(id, name) values(1004, 'page/longReport');
insert into uicontrols(id, name) values(1005, 'page/searchResults');
insert into uicontrols(id, name) values(1006, 'page/dashboard');
insert into uicontrols(id, name) values(1007, 'page/usa-map');
insert into uicontrols(id, name) values(1008, 'page/chart-drill-down');
insert into uicontrols(id, name) values(1009, 'page/longview/');
insert into uicontrols(id, name) values(1010, 'page/longview');
insert into uicontrols(id, name) values(1011, 'page/page/reports/edit/');
insert into uicontrols(id, name) values(1012, 'page/search/details/');
insert into uicontrols(id, name) values(1013, 'page/report/upload');
insert into uicontrols(id, name) values(1014, 'page/chart1');
insert into uicontrols(id, name) values(1015, 'page/chart2');
insert into uicontrols(id, name) values(1016, 'page/reports/grid');
insert into uicontrols(id, name) values(1017, 'page/reports/markdown-submit');
insert into uicontrols(id, name) values(1018, 'page/reports/pdf-viewer');
insert into uicontrols(id, name) values(1019, 'page/reports/tab-group');
insert into uicontrols(id, name) values(1020, 'page/reports/search');


-- Assign ui controls for the 'admin' role
insert into roles_uicontrols(role_id, uicontrol_id) values(1, 1001);
insert into roles_uicontrols(role_id, uicontrol_id) values(1, 1002);
insert into roles_uicontrols(role_id, uicontrol_id) values(1, 1003);
insert into roles_uicontrols(role_id, uicontrol_id) values(1, 1004);
insert into roles_uicontrols(role_id, uicontrol_id) values(1, 1005);
insert into roles_uicontrols(role_id, uicontrol_id) values(1, 1006);
insert into roles_uicontrols(role_id, uicontrol_id) values(1, 1007);
insert into roles_uicontrols(role_id, uicontrol_id) values(1, 1008);
insert into roles_uicontrols(role_id, uicontrol_id) values(1, 1009);
insert into roles_uicontrols(role_id, uicontrol_id) values(1, 1010);
insert into roles_uicontrols(role_id, uicontrol_id) values(1, 1011);
insert into roles_uicontrols(role_id, uicontrol_id) values(1, 1012);
insert into roles_uicontrols(role_id, uicontrol_id) values(1, 1013);
insert into roles_uicontrols(role_id, uicontrol_id) values(1, 1014);
insert into roles_uicontrols(role_id, uicontrol_id) values(1, 1015);
insert into roles_uicontrols(role_id, uicontrol_id) values(1, 1016);
insert into roles_uicontrols(role_id, uicontrol_id) values(1, 1017);
insert into roles_uicontrols(role_id, uicontrol_id) values(1, 1018);
insert into roles_uicontrols(role_id, uicontrol_id) values(1, 1019);
insert into roles_uicontrols(role_id, uicontrol_id) values(1, 1020);


-- Assign ui controls for the 'reader' role  (cannot get to addReport)
insert into roles_uicontrols(role_id, uicontrol_id) values(2, 1001);
insert into roles_uicontrols(role_id, uicontrol_id) values(2, 1002);
insert into roles_uicontrols(role_id, uicontrol_id) values(2, 1003);
insert into roles_uicontrols(role_id, uicontrol_id) values(2, 1004);
insert into roles_uicontrols(role_id, uicontrol_id) values(2, 1005);
insert into roles_uicontrols(role_id, uicontrol_id) values(2, 1006);
insert into roles_uicontrols(role_id, uicontrol_id) values(2, 1007);
insert into roles_uicontrols(role_id, uicontrol_id) values(2, 1008);
insert into roles_uicontrols(role_id, uicontrol_id) values(2, 1009);
insert into roles_uicontrols(role_id, uicontrol_id) values(2, 1010);
insert into roles_uicontrols(role_id, uicontrol_id) values(2, 1011);
insert into roles_uicontrols(role_id, uicontrol_id) values(2, 1012);
insert into roles_uicontrols(role_id, uicontrol_id) values(2, 1013);
insert into roles_uicontrols(role_id, uicontrol_id) values(2, 1014);
insert into roles_uicontrols(role_id, uicontrol_id) values(2, 1015);
insert into roles_uicontrols(role_id, uicontrol_id) values(2, 1016);
insert into roles_uicontrols(role_id, uicontrol_id) values(2, 1017);
insert into roles_uicontrols(role_id, uicontrol_id) values(2, 1018);
insert into roles_uicontrols(role_id, uicontrol_id) values(2, 1019);
insert into roles_uicontrols(role_id, uicontrol_id) values(2, 1020);
