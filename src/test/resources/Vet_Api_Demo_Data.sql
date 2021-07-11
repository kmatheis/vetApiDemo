-- Server Keys
insert into server_keys( server_key ) values( 'zuefpCdcIz/8VEkflD0D+K5srLeunWU+GnGzMMhzyHE=' );

-- Roles
insert into roles values( '1', 'ADMIN' );
insert into roles values( '2', 'RECEPTIONIST' );
insert into roles values( '3', 'TECHNICIAN' );

-- Privs (Receptionist cannot delete profile, animal, nor owner. Technician can all comments and delete animals.)
insert into privs( description, role_id ) values( 'all users', 1 );
insert into privs( description, role_id ) values( 'all animals', 1 );
insert into privs( description, role_id ) values( 'all profiles', 1 );
insert into privs( description, role_id ) values( 'all owners', 1 );
insert into privs( description, role_id ) values( 'all reservations', 1 );
insert into privs( description, role_id ) values( 'add animals', 2 );
insert into privs( description, role_id ) values( 'edit animals', 2 );
insert into privs( description, role_id ) values( 'read animals', 2 );
insert into privs( description, role_id ) values( 'add profiles', 2 );
insert into privs( description, role_id ) values( 'edit profiles', 2 );
insert into privs( description, role_id ) values( 'read profiles', 2 );
insert into privs( description, role_id ) values( 'add owners', 2 );
insert into privs( description, role_id ) values( 'edit owners', 2 );
insert into privs( description, role_id ) values( 'read owners', 2 );
insert into privs( description, role_id ) values( 'all reservations', 2 );
insert into privs( description, role_id ) values( 'del animals', 3 );

-- Users (should never be deleteable nor modifiable)
insert into users( username, hash, role_id ) values( 'vetroot', '$2a$10$3jdN5MqO9tWHjTO8JGhU8.ACs9TloXqR.YjCB84d8SOB1USzSYV5.', 1 );
insert into users( username, hash, role_id ) values( 'vetrec', '$2a$10$SBYd3h17NqVt3BrM5XJ87ugPDFC5YmtKCqbvNf5X8UdVweR1/Hooi', 2 );
insert into users( username, hash, role_id ) values( 'vettech', '$2a$10$1TJR8h8zG8RoXSEysfehJe7qbcluEzJZxUkO09UTUL.HZezaGwZRi', 3 );

-- Procs (H2 has an issue with stored procedures)

-- delimiter $$
-- drop procedure if exists find_users$$
-- create procedure find_users( in str varchar( 40 ) )
-- begin 
--  select * from users where username like concat( '%', str, '%' ) order by id; 
-- end
-- $$
-- delimiter ;

-- Rooms
insert into rooms( id, name, maxcap, cost ) values( 101, 'Gray Brick House', 1, 50.00 );
insert into rooms( id, name, maxcap, cost ) values( 102, 'Red Brick House', 1, 50.00 );
insert into rooms( id, name, maxcap, cost ) values( 103, 'Tropical Paradise', 2, 60.00 );
insert into rooms( id, name, maxcap, cost ) values( 104, 'Brown Fuzz', 2, 55.00 );
insert into rooms( id, name, maxcap, cost ) values( 105, 'Lunar Landscape', 3, 75.00 );
insert into rooms( id, name, maxcap, cost ) values( 106, 'Treehouse Colony', 3, 75.00 );

-- Profiles
insert into profiles( id, name ) values( 1001, 'M/C Household' );
insert into profiles( id, name ) values( 1002, 'Jana Household' );

-- Animals
insert into animals( id, name, species, profile_fk ) values( 10001, 'Sunshine', 'CAT', 1 );
insert into animals( id, name, species, profile_fk ) values( 10002, 'Mittens', 'CAT', 1 );
insert into animals( id, name, species, profile_fk ) values( 10003, 'Iana', 'SUGAR_GLIDER', 1 ); 
insert into animals( id, name, species, profile_fk ) values( 10004, 'Torrence', 'TORTOISE', 2 );
insert into animals( id, name, species, profile_fk ) values( 10005, 'Myrtle', 'TURTLE', 2 );

-- Owners
insert into owners( id, name, phone, profile_fk ) values( 5001, 'Ken', '800-555-1212', 1 );
insert into owners( id, name, phone, profile_fk ) values( 5002, 'Sylvia', '813-555-1234', 1 );
insert into owners( id, name, phone, profile_fk ) values( 5003, 'Jana', '813-555-0059', 2 );
insert into owners( id, name, phone, profile_fk ) values( 5004, 'Jenny', '813-867-5309', 2 );

-- Reservations
insert into reservations values( 4, 1, '2021-06-01', '2021-06-04', 0 );
insert into reservations values( 4, 1, '2021-06-11', '2021-06-15', 0 );
insert into reservations values( 1, 6, '2021-04-01', '2021-04-05', 0 );
insert into reservations values( 2, 6, '2021-04-01', '2021-04-05', 0 );
insert into reservations values( 3, 6, '2021-04-01', '2021-04-05', 0 );
