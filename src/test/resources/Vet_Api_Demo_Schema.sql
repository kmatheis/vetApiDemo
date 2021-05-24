drop table if exists users;
drop table if exists privs;
drop table if exists roles;
drop table if exists server_keys;

create table if not exists server_keys (
    id int unsigned not null auto_increment,
    server_key varchar(90),
    primary key( id )
);

-- e.g., ADMIN, TECH, RECEPTIONIST
create table if not exists roles (
    id int unsigned not null auto_increment,
    rolename varchar(20),
    primary key( id )
);

-- e.g., add users, edit animals, read profiles, del reservations, all users
create table if not exists privs (
    id int unsigned not null auto_increment,
    description varchar(20) not null,
    role_id int unsigned not null,
    primary key( id ),
    foreign key( role_id ) references roles( id )
);

create table if not exists users (
    id int unsigned not null auto_increment,
    username varchar(40) not null,
    hash varchar(255) not null,
    role_id int unsigned not null,
    primary key( id ),
    unique key( username ),
    foreign key( role_id ) references roles( id )
);