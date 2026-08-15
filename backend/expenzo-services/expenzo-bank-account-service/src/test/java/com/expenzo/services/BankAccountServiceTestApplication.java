package com.expenzo.services;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

/**
 * Test bootstrap configuration for the expenzo-bank-account-service module.
 * Spring Boot test slices (e.g. @DataJpaTest) require a @SpringBootConfiguration
 * with auto-configuration to load; library modules have no @SpringBootApplication,
 * so this marker provides one.
 */
@SpringBootConfiguration
@EnableAutoConfiguration
public class BankAccountServiceTestApplication {
}
