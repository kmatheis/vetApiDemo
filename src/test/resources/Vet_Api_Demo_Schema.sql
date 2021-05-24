drop table if exists server_keys;

create table if not exists server_keys (
    id int unsigned not null auto_increment,
    server_key varchar(90),
    primary key( id )
);