create table accounts (
                          "ID"      integer auto_increment,
                          "NUMBER"  integer,
                          "NAME"    varchar(255),
                          "BALANCE" double,
                          PRIMARY KEY ("ID")
);
create table Jwtdetails
(
    "ID"         integer auto_increment,
    "USERNAME"   varchar(255),
    "PASSWORD"   varchar(255),
    "LOGOUT"     Boolean,
    "EXPIRATION" TIMESTAMP,
    PRIMARY KEY ("ID")
);