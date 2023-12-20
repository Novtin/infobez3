package javaClasses;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Главный класс приложения.
 */
@SpringBootApplication
@ComponentScans(value = { @ComponentScan("javaClasses.config"),
        @ComponentScan("javaClasses.entity"), @ComponentScan("javaClasses.repository"),
        @ComponentScan("javaClasses.service")})
@EnableJpaRepositories(value = "javaClasses.repository")
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
