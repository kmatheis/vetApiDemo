-- Server Keys
insert into server_keys( server_key ) values( 'zuefpCdcIz/8VEkflD0D+K5srLeunWU+GnGzMMhzyHE=' );

-- Roles
insert into roles values( '1', 'ADMIN' );

-- Privs
insert into privs( description, role_id ) values( 'all users', 1 );
insert into privs( description, role_id ) values( 'all animals', 1 );

-- Users
insert into users( username, hash, role_id ) values( 'vetroot', '$2a$10$3jdN5MqO9tWHjTO8JGhU8.ACs9TloXqR.YjCB84d8SOB1USzSYV5.', 1 );