package edu.ntnu.idi.idatt2105.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main Spring Boot application entry point for the backend.
 * <p>
 * Enables scheduling and JPA auditing for the application and starts the embedded
 * Spring Boot container.
 */
@EnableScheduling
@SpringBootApplication(excludeName = "org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration")
@EnableJpaAuditing
public class BackendApplication {

	/**
	 * Boots the Spring application context for the backend service.
	 *
	 * @param args command line arguments passed to the application
	 */
	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

}
