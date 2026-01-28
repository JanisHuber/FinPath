package ch.finpath.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.converter.json.GsonBuilderUtils;

@SpringBootApplication
public class FinPathApplication {
    public static void main(String[] args) {
        System.out.println(System.getenv("JDBC_URL"));
        System.out.println(System.getenv("SUPABASE_AUTH_URL"));
        SpringApplication.run(FinPathApplication.class, args);
    }
}
