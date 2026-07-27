package com.example.swinecore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@EnableScheduling
public class SwineCoreApplication {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(SwineCoreApplication.class);

        // Safe localhost defaults. Environment variables still allow deployment overrides.
        // Keeping these defaults here also prevents startup failure if an extracted ZIP's
        // application.properties is accidentally skipped by an IDE or launcher.
        Map<String, Object> defaults = new HashMap<>();
        defaults.put("spring.datasource.url", env("SPRING_DATASOURCE_URL",
                "jdbc:mysql://localhost:3306/swinecore?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&createDatabaseIfNotExist=true&zeroDateTimeBehavior=CONVERT_TO_NULL"));
        defaults.put("spring.datasource.username", env("SPRING_DATASOURCE_USERNAME", "root"));
        defaults.put("spring.datasource.password", env("SPRING_DATASOURCE_PASSWORD", "root"));
        defaults.put("spring.datasource.driver-class-name", "com.mysql.cj.jdbc.Driver");
        defaults.put("spring.jpa.hibernate.ddl-auto", "update");
        defaults.put("server.address", "0.0.0.0");
        application.setDefaultProperties(defaults);
        application.run(args);
    }

    private static String env(String name, String fallback) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? fallback : value;
    }
}
