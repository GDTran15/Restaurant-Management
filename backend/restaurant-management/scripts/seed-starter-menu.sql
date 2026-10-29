-- Sample prices use the restaurant's configured currency.
-- Adds records without overwriting existing foods or changing the active menu.
BEGIN;

CREATE TEMP TABLE starter_foods (
    name TEXT, category TEXT, description TEXT, price NUMERIC(19,2),
    stock INTEGER, image TEXT
) ON COMMIT DROP;

INSERT INTO starter_foods VALUES
('Beef Belly', 'Beef', 'Thinly sliced beef belly, ready for the grill with a savoury house marinade.', 16.50, 40, 'Beef-Belly.jpg'),
('Premium Beef Belly', 'Beef', 'Tender premium beef belly with rich marbling and a light sesame seasoning.', 24.00, 30, 'Premium-Beef-Belly.jpg'),
('Wagyu Tomahawk', 'Beef', 'A generous bone-in wagyu steak for sharing, served with house dipping sauce.', 59.00, 15, 'Wagyu-Beef-Tomahawk.jpg'),
('Pork Belly', 'Pork', 'Juicy pork belly slices with a sweet and savoury barbecue marinade.', 15.00, 40, 'Pork-Belly.jpg'),
('Grilled Squid', 'Seafood', 'Tender squid with garlic, lemon and a lightly seasoned glaze.', 18.00, 30, 'Squid.jpg'),
('Grilled Octopus', 'Seafood', 'Chargrilled octopus with a bright citrus dressing and a hint of chilli.', 22.00, 25, 'Octopus.jpg'),
('Enoki Mushrooms', 'Vegetables', 'Delicate enoki mushrooms with garlic butter and a light soy dressing.', 8.50, 50, 'Enoki-Mushroom.webp'),
('House Burger', 'Burgers', 'A grilled beef patty with melted cheese, crisp lettuce and house sauce.', 17.50, 35, 'burger.png'),
('Chocolate Ice Cream', 'Desert', 'Rich chocolate ice cream, a cool finish to your meal.', 6.50, 60, 'Ice-Cream-Chocolate.jpg'),
('Mochi', 'Desert', 'Soft, chewy mochi with a sweet filling. A small treat for sharing.', 7.00, 50, 'mochi.png'),
('Coca-Cola', 'Drink', 'Chilled Coca-Cola, served over ice.', 3.50, 100, 'Coke.jpg'),
('Sprite', 'Drink', 'A refreshing lemon-lime soft drink, served chilled.', 3.50, 100, 'Sprite.jpg');

INSERT INTO food_category (food_category_name)
SELECT DISTINCT category FROM starter_foods
ON CONFLICT (food_category_name) DO NOTHING;

INSERT INTO foods (food_name, is_available, description, quantity, food_image_url, price)
SELECT name, true, description, stock, image, price FROM starter_foods
ON CONFLICT (food_name) DO NOTHING;

INSERT INTO food_category_map (food_id, food_category_id)
SELECT f.food_id, c.food_category_id
FROM starter_foods s
JOIN foods f ON f.food_name = s.name
JOIN food_category c ON c.food_category_name = s.category
ON CONFLICT (food_id) DO NOTHING;

INSERT INTO menus (menu_name, menu_desc, is_activated) VALUES
('Lunch Favourites', 'An easy lunch selection of grilled favourites, a house burger, dessert and refreshing drinks.', false),
('BBQ Dinner', 'A sharing menu of premium beef, pork, seafood and vegetables, followed by sweet treats.', false)
ON CONFLICT (menu_name) DO NOTHING;

-- Do not modify either menu if it was activated after a previous run.
INSERT INTO menu_items (food_id, menu_id)
SELECT f.food_id, m.menu_id
FROM starter_foods s
JOIN foods f ON f.food_name = s.name
JOIN menus m ON m.menu_name IN ('Lunch Favourites', 'BBQ Dinner')
WHERE NOT m.is_activated
AND (
    (m.menu_name = 'BBQ Dinner' AND s.name <> 'House Burger')
    OR (m.menu_name = 'Lunch Favourites' AND s.name IN (
        'Beef Belly', 'Pork Belly', 'Enoki Mushrooms', 'House Burger',
        'Chocolate Ice Cream', 'Coca-Cola', 'Sprite'
    ))
)
ON CONFLICT (menu_id, food_id) DO NOTHING;

COMMIT;

SELECT m.menu_name, m.is_activated, COUNT(mi.menu_item_id) AS items
FROM menus m LEFT JOIN menu_items mi ON mi.menu_id = m.menu_id
GROUP BY m.menu_id ORDER BY m.menu_id;
