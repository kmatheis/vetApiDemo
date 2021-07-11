drop table if exists comments;
drop table if exists reservations;
drop table if exists owners;
drop table if exists animals;
drop table if exists profiles;
drop table if exists rooms;
drop table if exists users;
drop table if exists privs;
drop table if exists roles;
drop table if exists server_keys;

create table if not exists server_keys (
    id int unsigned not null auto_increment,
    server_key varchar(90),
    primary key ( id )
);

-- e.g., ADMIN, TECHNICIAN, RECEPTIONIST
create table if not exists roles (
    id int unsigned not null auto_increment,
    rolename varchar(20),
    primary key ( id )
);

-- e.g., add users, edit animals, read profiles, del reservations, all users
create table if not exists privs (
    id int unsigned not null auto_increment,
    description varchar(20) not null,
    role_id int unsigned not null,
    primary key ( id ),
    foreign key ( role_id ) references roles ( id )
);

create table if not exists users (
    id int unsigned not null auto_increment,
    username varchar(40) not null,
    hash varchar(255) not null,
    role_id int unsigned not null,
    primary key ( id ),
    unique key ( username ),
    foreign key ( role_id ) references roles ( id )
);

create table if not exists rooms (
    pk int unsigned not null auto_increment,
    id int unsigned not null,
    name varchar(40) not null,
    maxcap int unsigned not null,
    cost decimal( 5, 2 ) not null,
    primary key ( pk )
);

create table if not exists profiles (
    pk int unsigned not null auto_increment,
    id int unsigned not null,
    name varchar(40) not null,
    primary key ( pk )
);

create table if not exists animals (
    pk int unsigned not null auto_increment,
    id int unsigned not null,
    name varchar(40) not null,
    species enum( 'OTHER', 'CAT', 'DOG', 'SUGAR_GLIDER', 'TURTLE', 'TORTOISE', 'IGUANA', 'BIRD', 'RODENT' ),
    profile_fk int unsigned not null,
    primary key ( pk ),
    foreign key ( profile_fk ) references profiles( pk ) on delete cascade
);

create table if not exists owners (
    pk int unsigned not null auto_increment,
    id int unsigned not null,
    name varchar(40) not null,
    phone varchar(40),
    profile_fk int unsigned not null,
    primary key ( pk ),
    foreign key ( profile_fk ) references profiles( pk ) on delete cascade
);

-- Implicit many-to-many table: res ids are dynamically generated instead of statically stored.
-- This is just to demo how to manage many-to-many without an explicit reservation entity.
create table if not exists reservations (
    animal_fk int unsigned not null,
    room_fk int unsigned not null,
    fromdate date not null,
    todate date not null,
    paid tinyint not null default 0,
    foreign key ( animal_fk ) references animals ( pk ) on delete cascade,
    foreign key ( room_fk ) references rooms( pk ) on delete cascade,
    unique key ( animal_fk, room_fk, fromdate )
);

create table if not exists comments (
    pk int unsigned not null auto_increment,
    id int unsigned not null,
    created datetime not null default now(),
    type enum( 'OTHER', 'VAX', 'CHECKUP', 'SURGERY' ),
    animal_fk int unsigned not null,
    primary key( pk ),
    foreign key ( animal_fk ) references animals( pk ) on delete cascade
);