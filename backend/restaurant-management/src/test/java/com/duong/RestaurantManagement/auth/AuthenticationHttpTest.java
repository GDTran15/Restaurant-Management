package com.duong.RestaurantManagement.auth;

import com.duong.RestaurantManagement.auth.service.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import tools.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.*;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"spring.datasource.url=${AUTH_DATABASE_URL}", "spring.jpa.show-sql=false"})
@ActiveProfiles("test")
@EnabledIfEnvironmentVariable(named = "AUTH_DATABASE_URL", matches = ".+")
class AuthenticationHttpTest {
    @LocalServerPort int port;
    @Autowired JdbcTemplate db;
    @Autowired JwtService jwt;
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper json = new ObjectMapper();

    @Test
    void loginRefreshLogoutRevokesPersistedTokenAndClearsCookie() throws Exception {
        String name = "auth-" + UUID.randomUUID();
        Long id = db.queryForObject("INSERT INTO users(username,email,password,phone,role) VALUES (?,?,?,?, 'ADMIN') RETURNING user_id",
                Long.class, name, name + "@test.invalid", new BCryptPasswordEncoder().encode("test-password"), 190010099);
        try {
            assertEquals(401, request("GET", "/orders/pending", null, null, null).statusCode());
            assertEquals(401, request("GET", "/orders/pending", "invalid", null, null).statusCode());
            assertEquals(401, login(name, "wrong").statusCode());
            var login = login(name, "test-password");
            assertEquals(200, login.statusCode(), login.body());
            String access = json.readTree(login.body()).get("accessToken").asText();
            String cookie = login.headers().firstValue("set-cookie").orElseThrow();
            assertTrue(cookie.contains("HttpOnly"));
            assertTrue(cookie.contains("SameSite=Strict"));
            String refresh = cookie.substring("refreshToken=".length(), cookie.indexOf(';'));
            String stored = db.queryForObject("SELECT token FROM refresh_tokens WHERE user_id=?", String.class, id);
            assertNotEquals(refresh, stored);
            assertTrue(stored.matches("[a-f0-9]{64}"));
            assertEquals(401, request("GET", "/orders/pending", refresh, null, null).statusCode());
            assertEquals(200, request("GET", "/orders/pending", access, null, null).statusCode());
            assertEquals(200, request("POST", "/refresh-token", "expired-access", refresh, null).statusCode());
            assertEquals(401, request("POST", "/refresh-token", null, null, null).statusCode());
            assertEquals(401, request("POST", "/refresh-token", null, "invalid", null).statusCode());
            assertEquals(401, request("POST", "/refresh-token", null, access, null).statusCode());
            String untracked = jwt.generateRefreshToken(User.withUsername(name).password("unused").roles("ADMIN").build());
            assertEquals(401, request("POST", "/refresh-token", null, untracked, null).statusCode());
            var secondLogin = login(name, "test-password");
            String secondCookie = secondLogin.headers().firstValue("set-cookie").orElseThrow();
            String second = secondCookie.substring("refreshToken=".length(), secondCookie.indexOf(';'));
            assertNotEquals(refresh, second);
            var logout = request("POST", "/logout", "expired-access", refresh, null);
            assertEquals(204, logout.statusCode());
            String clearedCookie = logout.headers().firstValue("set-cookie").orElseThrow();
            assertEquals(0, java.net.HttpCookie.parse(clearedCookie).getFirst().getMaxAge(), clearedCookie);
            assertEquals(Boolean.TRUE, db.queryForObject("SELECT is_revoked FROM refresh_tokens WHERE token=?", Boolean.class, stored));
            assertEquals(401, request("POST", "/refresh-token", null, refresh, null).statusCode());
            assertEquals(200, request("POST", "/refresh-token", null, second, null).statusCode());
            assertEquals(204, request("POST", "/logout", null, refresh, null).statusCode());
            assertEquals(204, request("POST", "/logout", null, null, null).statusCode());
            db.update("UPDATE refresh_tokens SET expire_at=CURRENT_TIMESTAMP - INTERVAL '1 minute' WHERE user_id=?", id);
            assertEquals(401, request("POST", "/refresh-token", null, second, null).statusCode());
        } finally {
            db.update("DELETE FROM refresh_tokens WHERE user_id=?", id);
            db.update("DELETE FROM users WHERE user_id=?", id);
        }
    }

    private HttpResponse<String> login(String username, String password) throws Exception {
        return request("POST", "/authenticate", null, null,
                json.writeValueAsString(java.util.Map.of("username", username, "password", password)));
    }
    private HttpResponse<String> request(String method, String path, String bearer, String cookie, String body) throws Exception {
        var request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + path));
        if (bearer != null) request.header("Authorization", "Bearer " + bearer);
        if (cookie != null) request.header("Cookie", "refreshToken=" + cookie);
        if (body != null) request.header("Content-Type", "application/json");
        return client.send(request.method(method, body == null ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(body)).build(), HttpResponse.BodyHandlers.ofString());
    }
}
