SET search_path TO streamix_email;

CREATE TABLE email_log (
    id            UUID         NOT NULL DEFAULT gen_random_uuid(),
    user_id       UUID         NOT NULL,
    type          VARCHAR(50)  NOT NULL,
    status        VARCHAR(10)  NOT NULL,
    created_at    TIMESTAMPTZ  NOT NULL,
    CONSTRAINT pk_email_log PRIMARY KEY (id)
);