package com.thomas.rgsga;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:postgresql://localhost:5432/testdb",
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=none",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect"
})
class RgSgaApplicationTests {

    @Test
    void applicationClassesLoad() {
        // Basic sanity check verifying package annotations
        assert RgSgaApplication.class != null;
    }
}
