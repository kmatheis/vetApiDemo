# Welcome to VetApiDemo!
This project is meant to demonstrate a backend Web API using Spring Boot. This API models common use cases in a veterinarian's office which also boards pets while their owners are away.

## Security Features
This demo has certain security features: each user is assigned a particular role, and each users must log in. Only hashes of passwords are stored in the database. 
Upon successful logging in, they will receive a JSON Web Token (JWT) in the Authorization header of the response. Users must then incorporate this bearer token in the Authorization header of any future requests.

Further, the project incorporates dynamic database roles: administrators need only change data in the database tables `privs` and `roles`  to tailor individual roles. The code will not have to change.

Finally, primary keys are hidden, and instead users use ids to manipulate profiles, animals, owners, and reservations. (In the case of users, the id is the primary key. This is simply to demo simpler code.) Each DAO which needs to will generate the next id when additional ids are called for. (At present, the scheme is simple, but one can do many things here.)

## Roles

The following roles are currently defined:
- ADMIN, which can do everything (including manipulate user logins),
- RECEPTIONIST, which cannot manipulate users nor comments, but can add/edit/read profiles, animals, and owners. They also can fully manipulate reservations, and
- TECHNICIAN, which can fully manipulate comments and delete animals.

The logins of
- vetroot/root,
- vetrec/vetrec, and
- vettech/vettech

do what you think they do. The system will reject attempts to mutate them. Other user logins that get added, however, should be fully mutable by those using an admin login.

## Incompleteness

This project is incomplete. The following will still need to be addressed:
- There are currently no ways to manipulate owners,
- There is no comment handling as yet (vax records, checkup notes, etc.),
- Dates are not synchronized to the correct timezone,
- Verification beans should be added in a few natural places,
- More endpoints need OpenAPI documentation,
- Additional reservation date checks need to be incorporated, and
- Not every CRUD operation is implemented with every entity.

The unit tests, though useful, can also stand to be a little more robust. 
These gaps will hopefully be addressed soon.

## API Calls

The following calls are supported:

| Action | Endpoint | Example JSON |
|--|--|--|
| POST | /users/login | `{ "username": "vetroot", "password": "root" }` |
| GET | /users |  |
| GET | /someusers?namecontains= |  |
| DELETE | /users/:id | |
| POST | /users | `{ "username": "alice", "password": "mypw", "rolename": "ADMIN" }`|
| PUT | /users/:id | `{ "username": "bob", "password": "myotherpw", "rolename": "RECEPTIONIST" }`|
| GET | /profiles | |
| GET | /profiles/:id | |
| PUT | /profiles/:id | `{ "name": "The Smith Family" }` |
| GET | /profiles/:pid/animals | |
| POST | /profiles/:pid/animals | `{ "name": "Rex", "species": "SUGAR_GLIDER" }` |
| DELETE | /profiles/:pid/animals/:aid | |
| PUT | /profiles/:pid/animals/:aid | `{ "name": "Torty", "profileId": 1001 }` (this moves the animal to the new profile and changes its name)|
| GET | /reservations/animals/:aid | |
| POST | /reservations | `{ "aid" : 10004, "rid": 1006, "fromdate": "2021-03-20", "todate": "2021-04-01" }` |
