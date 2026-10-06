-- Do not silently delete or merge conflicting restaurant records. Constraint
-- failures must be resolved against a backup before retrying this migration.
ALTER TABLE foods ALTER COLUMN price TYPE NUMERIC(19,2) USING round(price::numeric, 2);
ALTER TABLE orders ALTER COLUMN order_price TYPE NUMERIC(19,2) USING round(order_price::numeric, 2);
ALTER TABLE order_items ALTER COLUMN total_price TYPE NUMERIC(19,2) USING round(total_price::numeric, 2);
ALTER TABLE order_items ADD COLUMN unit_price NUMERIC(19,2);
UPDATE order_items SET unit_price = round(total_price / NULLIF(quantity, 0), 2);
ALTER TABLE order_items ALTER COLUMN unit_price SET NOT NULL;
ALTER TABLE invoices ALTER COLUMN pay_before_discount TYPE NUMERIC(19,2) USING round(pay_before_discount::numeric,2);
ALTER TABLE invoices ALTER COLUMN discount_amount TYPE NUMERIC(19,2) USING round(discount_amount::numeric,2);
ALTER TABLE invoices ALTER COLUMN total_pay TYPE NUMERIC(19,2) USING round(total_pay::numeric,2);
ALTER TABLE members ALTER COLUMN total_spent TYPE NUMERIC(19,2) USING round(total_spent::numeric,2);
ALTER TABLE memberships ALTER COLUMN min_spent TYPE NUMERIC(19,2) USING round(min_spent::numeric,2);
ALTER TABLE memberships ALTER COLUMN discount_rate TYPE NUMERIC(7,6) USING discount_rate::numeric;
ALTER TABLE memberships ALTER COLUMN membership_id TYPE BIGINT;
ALTER TABLE payments ALTER COLUMN payment_id TYPE BIGINT;
ALTER TABLE payments ALTER COLUMN amount TYPE NUMERIC(19,2);
ALTER TABLE payments ALTER COLUMN amount SET NOT NULL;

-- Hibernate generated the old ordinal enum CHECK with a variable name.
DO $$ DECLARE c RECORD; BEGIN
 FOR c IN SELECT conname FROM pg_constraint WHERE conrelid = 'payments'::regclass
 AND contype = 'c' AND pg_get_constraintdef(oid) LIKE '%payment_status%'
 LOOP EXECUTE format('ALTER TABLE payments DROP CONSTRAINT %I', c.conname); END LOOP;
END $$;
ALTER TABLE payments ALTER COLUMN payment_status TYPE VARCHAR(255)
 USING CASE payment_status::text WHEN '0' THEN 'PENDING' WHEN '1' THEN 'COMPLETED' ELSE payment_status::text END;
ALTER TABLE payments ADD COLUMN currency VARCHAR(3);
-- Legacy payments did not record currency. Operators must provide the actual
-- historic restaurant currency explicitly for a populated database.
DO $$ BEGIN
 IF EXISTS (SELECT 1 FROM payments) THEN
  IF '${legacyCurrency}' !~ '^[A-Z]{3}$' THEN
   RAISE EXCEPTION 'Set spring.flyway.placeholders.legacyCurrency to the verified historic payment currency';
  END IF;
  UPDATE payments SET currency = '${legacyCurrency}';
 END IF;
END $$;
ALTER TABLE payments ALTER COLUMN currency SET NOT NULL;
ALTER TABLE payments ADD CONSTRAINT payment_status_valid CHECK (payment_status IN ('PENDING','CAPTURING','COMPLETED','CANCELLED'));
ALTER TABLE payments ADD CONSTRAINT payment_amount_valid CHECK (amount >= 0 AND cash_change >= 0);
ALTER TABLE payments ADD CONSTRAINT payment_currency_valid CHECK (currency ~ '^[A-Z]{3}$');
ALTER TABLE payments ADD CONSTRAINT paypal_order_unique UNIQUE(paypal_order_id);
ALTER TABLE payments ALTER COLUMN invoice_id SET NOT NULL;
ALTER TABLE payments ALTER COLUMN payment_status SET NOT NULL;
ALTER TABLE payments ALTER COLUMN payment_method SET NOT NULL;
CREATE UNIQUE INDEX one_completed_payment_per_invoice ON payments(invoice_id) WHERE payment_status = 'COMPLETED';
CREATE UNIQUE INDEX one_open_paypal_attempt ON payments(invoice_id) WHERE payment_status IN ('PENDING','CAPTURING');
ALTER TABLE dining_sessions ALTER COLUMN restaurant_table_id SET NOT NULL;
ALTER TABLE dining_sessions ALTER COLUMN dining_status SET NOT NULL;
ALTER TABLE orders ALTER COLUMN dining_session_id SET NOT NULL;
ALTER TABLE order_items ALTER COLUMN order_id SET NOT NULL;
ALTER TABLE order_items ALTER COLUMN food_id SET NOT NULL;
ALTER TABLE food_category_map ALTER COLUMN food_id SET NOT NULL;
ALTER TABLE food_category_map ALTER COLUMN food_category_id SET NOT NULL;
ALTER TABLE food_category_map ADD CONSTRAINT one_category_per_food UNIQUE(food_id);
ALTER TABLE menu_items ALTER COLUMN food_id SET NOT NULL;
ALTER TABLE menu_items ALTER COLUMN menu_id SET NOT NULL;
ALTER TABLE menu_items ADD CONSTRAINT menu_food_unique UNIQUE(menu_id,food_id);
ALTER TABLE invoices ALTER COLUMN dining_session_id SET NOT NULL;
ALTER TABLE invoices ALTER COLUMN invoice_status SET NOT NULL;
CREATE UNIQUE INDEX one_current_invoice_per_session ON invoices(dining_session_id) WHERE invoice_status <> 'CANCELLED';
UPDATE dining_sessions ds SET dining_status = 'WAITING_FOR_PAYMENT'
 WHERE dining_status = 'ACTIVE' AND EXISTS (SELECT 1 FROM invoices i WHERE i.dining_session_id = ds.dining_session_id AND i.invoice_status = 'UNPAID');
CREATE UNIQUE INDEX one_open_session_per_table ON dining_sessions(restaurant_table_id) WHERE dining_status <> 'COMPLETED';
CREATE UNIQUE INDEX one_active_menu ON menus(is_activated) WHERE is_activated;
ALTER TABLE restaurant_tables ADD CONSTRAINT table_number_unique UNIQUE(restaurant_table_number);
ALTER TABLE restaurant_tables ADD CONSTRAINT table_qr_unique UNIQUE(table_qr_code_value);
ALTER TABLE restaurant_tables ALTER COLUMN table_qr_code_value SET NOT NULL;
ALTER TABLE restaurant_tables ADD CONSTRAINT table_capacity_valid CHECK (capacity >= 2 AND restaurant_table_number > 0);
ALTER TABLE memberships ADD CONSTRAINT membership_rank_unique UNIQUE(membership_rank);
ALTER TABLE memberships ALTER COLUMN membership_rank SET NOT NULL;
ALTER TABLE memberships ADD CONSTRAINT membership_amount_valid CHECK(min_spent >= 0 AND discount_rate BETWEEN 0 AND 1);
ALTER TABLE members ADD CONSTRAINT member_phone_unique UNIQUE(member_phone);
ALTER TABLE members ADD CONSTRAINT member_email_unique UNIQUE(member_email);
ALTER TABLE members ADD CONSTRAINT member_spend_valid CHECK(total_spent >= 0);
ALTER TABLE foods ADD CONSTRAINT food_name_unique UNIQUE(food_name);
ALTER TABLE menus ADD CONSTRAINT menu_name_unique UNIQUE(menu_name);
ALTER TABLE food_category ADD CONSTRAINT category_name_unique UNIQUE(food_category_name);
ALTER TABLE foods ADD CONSTRAINT food_stock_valid CHECK(quantity >= 0 AND price > 0);
ALTER TABLE orders ADD CONSTRAINT order_price_valid CHECK(order_price >= 0);
ALTER TABLE order_items ADD CONSTRAINT order_item_valid CHECK(quantity > 0 AND total_price >= 0 AND unit_price >= 0);
ALTER TABLE invoices ADD CONSTRAINT invoice_amount_valid CHECK(pay_before_discount >= 0 AND discount_amount >= 0 AND discount_amount <= pay_before_discount AND total_pay = pay_before_discount - discount_amount);
CREATE INDEX orders_session_idx ON orders(dining_session_id);
CREATE INDEX order_items_order_idx ON order_items(order_id);
CREATE INDEX order_items_food_idx ON order_items(food_id);
CREATE INDEX food_category_map_category_idx ON food_category_map(food_category_id);
CREATE INDEX menu_items_food_idx ON menu_items(food_id);
CREATE INDEX invoices_member_idx ON invoices(member_id);
CREATE INDEX invoices_created_idx ON invoices(created_at);
CREATE INDEX payments_invoice_idx ON payments(invoice_id);
