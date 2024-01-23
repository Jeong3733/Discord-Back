DROP table IF EXISTS block_request;
DROP table IF EXISTS friendship_request;
DROP table IF EXISTS verification_token;
DROP table IF EXISTS user_server;
DROP table IF EXISTS server;
DROP table IF EXISTS user;

create table user
(
    email_authenticated bit                        not null,
    birth               datetime(6)                null,
    created_at          datetime(6)                null,
    id                  bigint auto_increment
        primary key,
    updated_at          datetime(6)                null,
    profile_image       binary(16)                 null,
    email               varchar(255)               null,
    nickname            varchar(255)               null,
    password            varchar(255)               null,
    profile_message     varchar(255)               null,
    role                enum ('ADMIN', 'USER')     null,
    user_status         enum ('ONLINE', 'OFFLINE') null,
    constraint UK_ob8kqyqqgmefl0aco34akdtpe
        unique (email)
);

create table server
(
    created_at    datetime(6)  null,
    id            bigint auto_increment
        primary key,
    updated_at    datetime(6)  null,
    profile_image binary(16)   null,
    description   varchar(255) null,
    name          varchar(255) null
);

create table user_server
(
    created_at  datetime(6)                null,
    id          bigint auto_increment
        primary key,
    server_id   bigint                     null,
    user_id     bigint                     null,
    user_status enum ('ONLINE', 'OFFLINE') null,
    constraint FK7fjhtmautm8iu1g22xkm2fyqf
        foreign key (user_id) references user (id),
    constraint FK8l2r2cexpon9udypq5y480sre
        foreign key (server_id) references server (id)
);

create table verification_token
(
    expiry_date datetime(6)  null,
    id          bigint auto_increment
        primary key,
    user_id     bigint       null,
    token       varchar(255) null,
    constraint UK_q6jibbenp7o9v6tq178xg88hg
        unique (user_id),
    constraint FKrdn0mss276m9jdobfhhn2qogw
        foreign key (user_id) references user (id)
);

create table block_request
(
    blocked_id   bigint      not null,
    created_at   datetime(6) null,
    id           bigint auto_increment
        primary key,
    requester_id bigint      not null,
    constraint FKbsstcoeyf9spirlx0xn7qm7sm
        foreign key (blocked_id) references user (id),
    constraint FKtaojqbqgb2d5j0vq498du5dsr
        foreign key (requester_id) references user (id)
);

create table friendship_request
(
    blocked_id   bigint                                   not null,
    created_at   datetime(6)                              null,
    id           bigint auto_increment
        primary key,
    requester_id bigint                                   not null,
    updated_at   datetime(6)                              null,
    status       enum ('PENDING', 'ACCEPTED', 'DECLINED') null,
    constraint FKq7pjwhljjxe6ljeaw4xtu4mky
        foreign key (blocked_id) references user (id),
    constraint FKrv8bwqoiltcgh5bgpfxqjxpuw
        foreign key (requester_id) references user (id)
);

