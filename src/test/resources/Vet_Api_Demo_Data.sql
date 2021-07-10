-- Server Keys
insert into server_keys( server_key ) values( 'zuefpCdcIz/8VEkflD0D+K5srLeunWU+GnGzMMhzyHE=' );

-- Roles
insert into roles values( '1', 'ADMIN' );

-- Privs
insert into privs( description, role_id ) values( 'all users', 1 );
insert into privs( description, role_id ) values( 'all animals', 1 );

-- Users
insert into users( username, hash, role_id ) values( 'vetroot', '$2a$10$3jdN5MqO9tWHjTO8JGhU8.ACs9TloXqR.YjCB84d8SOB1USzSYV5.', 1 );

-- Procs (H2 has an issue with stored procedures)

-- delimiter $$
-- drop procedure if exists find_users$$
-- create procedure find_users( in str varchar( 40 ) )
-- begin 
-- 	select * from users where username like concat( '%', str, '%' ) order by id; 
-- end
-- $$
-- delimiter ;

