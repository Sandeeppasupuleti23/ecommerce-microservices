create table if not exists orders (
    id uuid primary key,
    status varchar(32) not null,
    created_at timestamp with time zone not null
);