package ch.finpath.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "ch.finpath")
@EntityScan(basePackages = "ch.finpath")
@EnableJpaRepositories(basePackages = "ch.finpath")
public class FinPathApplication {
    public static void main(String[] args) {
        SpringApplication.run(FinPathApplication.class, args);
    }
}
