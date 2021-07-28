# Welcome to VetApiDemo!
This project is meant to demonstrate a backend Web API using Spring Boot (JDBC driver) and MySQL. This API models common use cases in a veterinarian's office which also boards pets while their owners are away.

This project was last updated on 2021-07-28.

## Security Features
This demo has certain security features: each user is assigned a particular role, and each users must log in. Only hashes of passwords are stored in the database. 
Upon successful logging in, they will receive a JSON Web Token (JWT) in the Authorization header of the response. Users must then incorporate this bearer token in the Authorization header of any future requests.

Further, the project incorporates dynamic database roles: administrators need only change data in the database tables `privs` and `roles`  to tailor individual roles; the code will not have to change.

Finally, primary keys are hidden, and instead users use ids to manipulate profiles, animals, owners, and reservations. (In the case of users, the id is the primary key. This is simply to demo simpler code.) 

Each DAO which needs to will generate the next id when additional ids are called for using a generator object, so that the code for next-id-generation is in only one place. (The scheme is simple, but one can do many things here.)

## Roles

The following roles are currently defined:
- ADMIN, which can do everything (including manipulate user logins).
- RECEPTIONIST, which cannot manipulate users, but can add/edit/read profiles, animals, and owners, and can read comments. They also can fully manipulate reservations.
- TECHNICIAN, which can fully manipulate comments and delete animals.

The logins of
- vetroot/root,
- vetrec/vetrec, and
- vettech/vettech

do what you think they do. The system will reject attempts to mutate them. Other user logins that get added, however, should be fully mutable by those using an admin login.

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
| GET | /animals/:aid | |
| GET | /profiles/:pid/owners | |
| POST | /profiles/:pid/owners | `{ "name": "Timmy", "phone": "800-555-9987" }` |
| DELETE | /profiles/:pid/owners/:oid | |
| PUT | /profiles/:pid/owners/:oid | `{ "name": "Tommy", "phone": "800-555-1267", "profileId": 1001 }` (this optionally moves the owner to the new profile and changes name and phone)|
| GET | /reservations/animals/:aid | |
| POST | /reservations | `{ "aid" : 10004, "rid": 1006, "fromdate": "2021-03-20", "todate": "2021-04-01" }` |
| POST | /animals/:aid/comments | `{ "ondate": "2021-06-04 14:14:14", "type": "CHECKUP", "comment": "This animal is healthy!" }` |
| DELETE | /animals/:aid/comments/:cid | |

Legal animal species are `OTHER`, `CAT`, `DOG`, `SUGAR_GLIDER`, `TURTLE`, `TORTOISE`, `IGUANA`, `BIRD`, and `RODENT`.

Legal comment types are `OTHER`, `VAX`, `CHECKUP`, `SURGERY`, `FEEDING` (which reflects what food was used when the animal was boarded), and `FOOD` (which reflects what food the vet sold the owner for this pet).

## Minor Notes

The dates used in reservations are stored as MySQL `DATE`s and deserialized into java.util.Date objects.

The datetimes used in comments are stored as MySQL `DATETIME`s and deserialized into java.sql.Timestamp objects. 

Though animals and owners will display owning profile id, comments will not display owning animal id. No one displays primary keys.

## Incompleteness

This project is incomplete. The following will still need to be addressed:
- Verification beans should be added in a few natural places,
- More endpoints need OpenAPI documentation, and
- Additional reservation date checks need to be incorporated.

The unit tests, though useful, can also stand to be a little more robust. 
These gaps will hopefully be addressed soon.
