package edu.eci.dosw.DOSW_Library;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories(
        basePackages = "edu.eci.dosw.DOSW_Library.persistence.nonrelational.repository"
)
public class DoswLibraryApplication {

    public static void main(String[] args) {
        SpringApplication.run(DoswLibraryApplication.class, args);
    }

    @Configuration
    @Profile("relational")
    @EnableJpaRepositories(
            basePackages = "edu.eci.dosw.DOSW_Library.persistence.relational.dao"
    )
    static class RelationalConfig {}
}