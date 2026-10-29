ALTER TABLE payments ADD COLUMN paypal_create_request_id VARCHAR(36);
ALTER TABLE payments ADD COLUMN paypal_create_requested_at TIMESTAMP WITH TIME ZONE;
ALTER TABLE payments ADD COLUMN paypal_approval_url VARCHAR(2048);
ALTER TABLE payments ADD CONSTRAINT paypal_create_request_unique UNIQUE (paypal_create_request_id);
