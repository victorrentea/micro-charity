package com.example.charityservice;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.stream.Stream;

@SpringBootApplication
public class CharityServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CharityServiceApplication.class, args);
	}

}

@RefreshScope
@RestController
class HelloController {

    @Value("${message}")
    private String message;

    @GetMapping("/hello")
	public String hello() {
    	return "Hello " + message;
	}
}

@Component
@RequiredArgsConstructor
class InsertDummyData implements CommandLineRunner {
	private final CharityRepository repo;

	@Override
	@Transactional
	public void run(String... args) throws Exception {
		Stream.of("Help an Elder", "Visit an Orphanage", "Donate $", "Comment your code")
			.map(Charity::new)
			.forEach(repo::save);
	}
}


@RepositoryRestResource(path = "charity")
interface CharityRepository extends JpaRepository<Charity, Long> {
}

@Data
@Entity
class Charity {
	@Id
	@GeneratedValue
	private Long id;
	private String name;
	private LocalDateTime creationTime = LocalDateTime.now();

	private Charity() {
	}

	public Charity(String name) {
		this.name = name;
	}
}
