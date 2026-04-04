package com.almina.ecommerce.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SchemaEnumSynchronizer implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        syncEnumColumns();
    }

    private void syncEnumColumns() {
        runAlter("UPDATE orders SET status = 'CONFIRMED' WHERE status IN ('PAID', 'PLACED')");
        runAlter("UPDATE orders SET status = 'CANCELLED' WHERE status = 'FAILED'");
        runAlter("UPDATE orders SET status = 'PENDING' WHERE status IS NULL OR status = ''");
        runAlter("UPDATE orders SET payment_status = 'SUCCESS' WHERE payment_status = 'PAID'");
        runAlter("UPDATE orders SET payment_status = 'FAILED' WHERE payment_status NOT IN ('PENDING','SUCCESS','FAILED') OR payment_status IS NULL OR payment_status = ''");
        runAlter("UPDATE orders SET payment_method = 'COD' WHERE payment_method NOT IN ('RAZORPAY','COD') OR payment_method IS NULL OR payment_method = ''");
        runAlter("UPDATE payments SET status = 'SUCCESS' WHERE status = 'PAID'");
        runAlter("UPDATE payments SET status = 'FAILED' WHERE status NOT IN ('PENDING','SUCCESS','FAILED') OR status IS NULL OR status = ''");
        runAlter("UPDATE payments SET provider = 'RAZORPAY' WHERE provider IN ('created', 'razorpay_created')");
        runAlter("UPDATE payments SET provider = 'COD' WHERE provider IS NULL OR provider = ''");
        runAlter("ALTER TABLE orders MODIFY status ENUM('PENDING','CONFIRMED','SHIPPED','DELIVERED','CANCELLED') NOT NULL");
        runAlter("ALTER TABLE orders MODIFY payment_status ENUM('PENDING','SUCCESS','FAILED') NOT NULL");
        runAlter("ALTER TABLE orders MODIFY payment_method ENUM('RAZORPAY','COD') NOT NULL");
        runAlter("ALTER TABLE payments MODIFY status ENUM('PENDING','SUCCESS','FAILED') NOT NULL");
        runAlter("ALTER TABLE payments MODIFY provider ENUM('RAZORPAY','COD') NOT NULL");
    }

    private void runAlter(String sql) {
        try {
            jdbcTemplate.execute(sql);
        } catch (Exception exception) {
            log.warn("Schema enum sync skipped for statement: {}. Cause: {}", sql, exception.getMessage());
        }
    }
}
