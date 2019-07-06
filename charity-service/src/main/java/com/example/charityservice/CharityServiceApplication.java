package com.example.charityservice;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.stream.Stream;

@SpringBootApplication
@EnableBinding(Sink.class)
//@EnableZip

public class CharityServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CharityServiceApplication.class, args);
    }

}

@RefreshScope
@RestController
class HelloController {
    @Value("${greeting}")
    private String greeting;

    @GetMapping("/hello")
    public String greet() {
        return greeting;
    }
}

@Component
@RequiredArgsConstructor
class InsertDummyData implements CommandLineRunner {
    private final CharityRepository repo;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        Stream.of("Orphan Help", "Donate Cloths", "Homeless Shelter Volunteer")
                .map(Charity::new)
                .forEach(repo::save);
    }
}

@RepositoryRestResource(path = "/charity")
interface CharityRepository extends JpaRepository<Charity, Long> {
}

@RequiredArgsConstructor
@MessageEndpoint
@Slf4j
class MessageHandler {
    private final CharityRepository repo;

    @ServiceActivator(inputChannel = Sink.INPUT)
    public void createCharity(String charityName) {
        log.info("Creating Charity");
        repo.save(new Charity(charityName));
    }
}


@Data
@Entity
class Charity {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private LocalDateTime creationTime = LocalDateTime.now();
    private Long heroId;

    private Charity() {
    }

    public Charity(String name) {
        this.name = name;
    }
}