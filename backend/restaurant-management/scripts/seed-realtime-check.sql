-- Run only in an empty disposable database after Flyway migrations.
CREATE EXTENSION IF NOT EXISTS pgcrypto;
INSERT INTO users(username,email,password,phone,role)
VALUES ('realtime-test','realtime@test.invalid',crypt('realtime-test-password',gen_salt('bf', 12)),190010001,'ADMIN');
INSERT INTO restaurant_tables(restaurant_table_number,capacity,table_qr_code_value,restaurant_table_status)
VALUES (7,4,'realtime-table-7',false), (8,4,'realtime-table-8',false);
INSERT INTO foods(food_name,is_available,quantity,price,food_image_url)
VALUES ('Realtime soup',true,100,6,'/Coke.jpg');
INSERT INTO menus(menu_name,menu_desc,is_activated) VALUES ('Realtime menu','Test only',true);
INSERT INTO menu_items(food_id,menu_id) SELECT food_id,menu_id FROM foods CROSS JOIN menus;
INSERT INTO food_category(food_category_name) VALUES ('Soup');
INSERT INTO food_category_map(food_id,food_category_id) SELECT food_id,food_category_id FROM foods CROSS JOIN food_category;
