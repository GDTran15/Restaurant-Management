package com.duong.RestaurantManagement;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import java.sql.DriverManager;
import java.util.Map;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

@EnabledIfEnvironmentVariable(named = "TEST_DATABASE_URL", matches = ".+")
class FlywayUpgradeTest {
    @Test
    void upgradesPopulatedLegacySchemaAndEnforcesRelationships() throws Exception {
        String url=System.getenv("TEST_DATABASE_URL");
        String user=System.getenv().getOrDefault("TEST_DATABASE_USERNAME","postgres");
        String password=System.getenv().getOrDefault("TEST_DATABASE_PASSWORD","");
        String schema="upgrade_test_"+UUID.randomUUID().toString().replace("-","");
        var initial=Flyway.configure().dataSource(url,user,password).schemas(schema).defaultSchema(schema)
                .target("1").placeholders(Map.of("legacyCurrency","USD")).load();
        initial.migrate();
        try (var connection=DriverManager.getConnection(url,user,password); var sql=connection.createStatement()) {
            sql.execute("SET search_path TO "+schema);
            sql.execute("INSERT INTO restaurant_tables VALUES (1,1,4,'legacy-qr',true)");
            sql.execute("INSERT INTO dining_sessions VALUES (1,now(),null,'ACTIVE',1)");
            sql.execute("INSERT INTO foods VALUES (1,'Soup',true,'Soup',10,'image',0.10)");
            sql.execute("INSERT INTO orders VALUES (1,now(),0.30,'COMPLETED','old-order',1)");
            sql.execute("INSERT INTO order_items VALUES (1,3,0.30,1,1)");
            sql.execute("INSERT INTO invoices VALUES (1,0.30,0,0.30,now(),'UNPAID',1,null)");
            sql.execute("INSERT INTO payments VALUES (1,0.30,null,'old-paypal',0,0,'PAYPAL',1)");
            var upgrade=Flyway.configure().dataSource(url,user,password).schemas(schema).defaultSchema(schema)
                    .placeholders(Map.of("legacyCurrency","USD")).load();
            upgrade.migrate();
            assertEquals(0,upgrade.migrate().migrationsExecuted);
            try(var rows=sql.executeQuery("SELECT unit_price FROM order_items")) {
                assertTrue(rows.next()); assertEquals("0.10",rows.getBigDecimal(1).toPlainString());
            }
            try(var rows=sql.executeQuery("SELECT payment_status,currency FROM payments")) {
                assertTrue(rows.next()); assertEquals("PENDING",rows.getString(1)); assertEquals("USD",rows.getString(2));
            }
            try(var rows=sql.executeQuery("SELECT dining_status FROM dining_sessions")) {
                assertTrue(rows.next()); assertEquals("WAITING_FOR_PAYMENT",rows.getString(1));
            }
            assertThrows(java.sql.SQLException.class,()->sql.execute("INSERT INTO dining_sessions VALUES (2,now(),null,'ACTIVE',1)"));
            assertThrows(java.sql.SQLException.class,()->sql.execute("INSERT INTO order_items VALUES (2,1,1,999,1,1)"));
            assertThrows(java.sql.SQLException.class,()->sql.execute("UPDATE foods SET quantity=-1"));
        } finally {
            try(var connection=DriverManager.getConnection(url,user,password);var sql=connection.createStatement()) {
                sql.execute("DROP SCHEMA "+schema+" CASCADE");
            }
        }
    }
}
