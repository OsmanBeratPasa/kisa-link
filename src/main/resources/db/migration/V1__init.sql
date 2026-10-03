Create  Table links(
id  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
code  VARCHAR(16) NOT NULL UNIQUE,
target_url  TEXT         NOT NULL,
click_count BIGINT       NOT NULL DEFAULT 0,
created_at  TIMESTAMPTZ  NOT NULL DEFAULT now()
);