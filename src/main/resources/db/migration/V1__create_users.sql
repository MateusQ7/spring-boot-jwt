create table users (
        id bigserial primary key,
        email varchar(255) not null unique,
        password_hash varchar(255) not null,
        role varchar(50) not null,
        active boolean not null default true,
        created_at timestamp not null default now()
);
