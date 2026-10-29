package com.duong.RestaurantManagement.order;

import com.duong.RestaurantManagement.auth.service.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

// Real HTTP, security, services and PostgreSQL. Use a disposable database.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"spring.datasource.url=${ORDER_API_DATABASE_URL}", "spring.jpa.show-sql=false"})
@ActiveProfiles("test")
@EnabledIfEnvironmentVariable(named = "ORDER_API_DATABASE_URL", matches = ".+")
class OrderStatusHttpTest {
    @LocalServerPort int port;
    @Autowired JdbcTemplate db;
    @Autowired JwtService jwt;
    private final ObjectMapper json = new ObjectMapper();
    private final HttpClient client = HttpClient.newHttpClient();

    @Test
    void threeActionsPersistAndInvalidTransitionsDoNotRestoreStockTwice() throws Exception {
        String suffix = UUID.randomUUID().toString();
        String username = "order-api-" + suffix;
        Long userId = null, tableId = null, sessionId = null, foodId = null;
        try {
            userId = db.queryForObject("INSERT INTO users(username,email,password,phone,role) VALUES (?,?,?,?, 'ADMIN') RETURNING user_id",
                    Long.class, username, suffix + "@test.invalid", new BCryptPasswordEncoder().encode("test-password"), 190010001);
            tableId = db.queryForObject("INSERT INTO restaurant_tables(restaurant_table_number,capacity,table_qr_code_value,restaurant_table_status) VALUES (190010001,4,?,false) RETURNING restaurant_table_id", Long.class, suffix);
            sessionId = db.queryForObject("INSERT INTO dining_sessions(start_at,dining_status,restaurant_table_id) VALUES (CURRENT_TIMESTAMP,'ACTIVE',?) RETURNING dining_session_id", Long.class, tableId);
            foodId = db.queryForObject("INSERT INTO foods(food_name,is_available,quantity,price) VALUES (?,true,6,10) RETURNING food_id", Long.class, "HTTP test " + suffix);
            long cooking = seedOrder(sessionId, foodId, "cook-" + suffix);
            long cancelled = seedOrder(sessionId, foodId, "cancel-" + suffix);
            var login = request("POST", "/authenticate", null,
                    json.writeValueAsString(java.util.Map.of("username", username, "password", "test-password")));
            assertEquals(200, login.statusCode(), login.body());
            String token = json.readTree(login.body()).get("accessToken").asText();

            assertEquals(401, patch(cooking, "IN_PROGRESS", null).statusCode());
            assertEquals(403, patch(cooking, "IN_PROGRESS", jwt.generateDiningToken(sessionId)).statusCode());
            assertEquals(409, patch(cooking, "COMPLETED", token).statusCode());
            assertEquals(204, patch(cooking, "IN_PROGRESS", token).statusCode());
            assertStatus(cooking, "IN_PROGRESS");
            assertListed("in-progress", cooking, token);
            assertEquals(204, patch(cooking, "COMPLETED", token).statusCode());
            assertStatus(cooking, "COMPLETED");
            assertListed("completed", cooking, token);
            assertEquals(204, patch(cancelled, "CANCELLED", token).statusCode());
            assertStatus(cancelled, "CANCELLED");
            assertListed("cancelled", cancelled, token);
            assertEquals(8, db.queryForObject("SELECT quantity FROM foods WHERE food_id=?", Integer.class, foodId));

            var repeat = patch(cancelled, "CANCELLED", token);
            assertEquals(409, repeat.statusCode());
            assertTrue(json.readTree(repeat.body()).get("detail").asText().contains("Cannot change"));
            assertEquals(8, db.queryForObject("SELECT quantity FROM foods WHERE food_id=?", Integer.class, foodId));
            assertEquals(409, patch(cooking, "CANCELLED", token).statusCode());
            assertEquals(400, patch(cooking, "UNKNOWN", token).statusCode());
            var counts = request("GET", "/orders/count/session", token, null);
            assertEquals(200, counts.statusCode());
            assertTrue(counts.body().contains("COMPLETED"));
        } finally {
            if (sessionId != null) {
                db.update("DELETE FROM order_items WHERE order_id IN (SELECT order_id FROM orders WHERE dining_session_id=?)", sessionId);
                db.update("DELETE FROM orders WHERE dining_session_id=?", sessionId);
                db.update("DELETE FROM dining_sessions WHERE dining_session_id=?", sessionId);
            }
            if (foodId != null) db.update("DELETE FROM foods WHERE food_id=?", foodId);
            if (tableId != null) db.update("DELETE FROM restaurant_tables WHERE restaurant_table_id=?", tableId);
            if (userId != null) db.update("DELETE FROM users WHERE user_id=?", userId);
        }
    }

    private long seedOrder(long sessionId, long foodId, String number) {
        long id = db.queryForObject("INSERT INTO orders(created_at,order_price,order_status,order_number,dining_session_id) VALUES (CURRENT_TIMESTAMP,20,'PENDING',?,?) RETURNING order_id", Long.class, number, sessionId);
        db.update("INSERT INTO order_items(quantity,total_price,unit_price,order_id,food_id) VALUES (2,20,10,?,?)", id, foodId);
        return id;
    }

    private void assertStatus(long id, String expected) {
        assertEquals(expected, db.queryForObject("SELECT order_status FROM orders WHERE order_id=?", String.class, id));
    }

    private void assertListed(String path, long id, String token) throws Exception {
        var response = request("GET", "/orders/" + path, token, null);
        assertEquals(200, response.statusCode(), response.body());
        boolean found = false;
        for (var order : json.readTree(response.body())) if (order.get("orderId").asLong() == id) found = true;
        assertTrue(found, "Order should appear in " + path);
    }

    private HttpResponse<String> patch(long id, String status, String token) throws Exception {
        return request("PATCH", "/orders/" + id + "/status?orderStatus=" + status, token, null);
    }

    private HttpResponse<String> request(String method, String path, String token, String body) throws Exception {
        var builder = HttpRequest.newBuilder(URI.create("http://localhost:" + port + path)).timeout(Duration.ofSeconds(15));
        if (token != null) builder.header("Authorization", "Bearer " + token);
        if (body != null) builder.header("Content-Type", "application/json");
        return client.send(builder.method(method, body == null ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(body)).build(), HttpResponse.BodyHandlers.ofString());
    }
}
