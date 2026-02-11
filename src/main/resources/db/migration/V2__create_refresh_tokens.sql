create table refresh_tokens (
                                id bigserial primary key,
                                user_id bigint not null references users(id) on delete cascade,
                                token_hash varchar(128) not null unique,
                                expires_at timestamp not null,
                                revoked_at timestamp null,
                                created_at timestamp not null default now()
);

create index idx_refresh_tokens_user_id on refresh_tokens(user_id);
