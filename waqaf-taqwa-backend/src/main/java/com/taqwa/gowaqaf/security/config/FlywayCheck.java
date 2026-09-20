package com.taqwa.gowaqaf.security.config;

import org.springframework.stereotype.Component;

@Component
public class FlywayCheck {

    public FlywayCheck(org.flywaydb.core.Flyway flyway) {
        System.out.println("========== FLYWAY BEAN EXISTS ==========");
        System.out.println(flyway);
    }
}
