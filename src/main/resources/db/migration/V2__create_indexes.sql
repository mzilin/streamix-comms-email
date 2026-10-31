SET search_path TO streamix_email;

CREATE INDEX idx_email_log_user_id ON email_log (user_id);
CREATE INDEX idx_email_log_sent_at ON email_log (sent_at);