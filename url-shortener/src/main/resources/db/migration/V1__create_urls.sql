create table urls (
                      id         bigint      generated always as identity primary key,
                      long_url   text        not null,
                      created_at timestamptz not null default now()
);
