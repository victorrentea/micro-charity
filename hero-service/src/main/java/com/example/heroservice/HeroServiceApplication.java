package com.example.heroservice;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Optional;
import java.util.stream.Stream;

@SpringBootApplication
public class HeroServiceApplication implements CommandLineRunner {
	public static void main(String[] args) {
		SpringApplication.run(HeroServiceApplication.class, args);
	}


	@Autowired
	private HeroRepository repo;
	@Override
	@Transactional
	public void run(String... args) throws Exception {
		Stream.of("Dalai Lama","Nelson Mandela","Parintele Tanase").map(Hero::new).forEach(repo::save);
	}
}


@Data
@RestController
@RequestMapping("hero")
class HeroController {
	private final HeroRepository repo;

	@GetMapping("id")
	public Long getByName(@RequestParam String name) {
		return repo.getByName(name).get().getId();
	}

//	@PostMapping
//	public Long create(@RequestBody String name) { // NEVER get/send Entity instances over REST in Production
//		return repo.save(new Hero(name)).getId();
//	}
}

interface HeroRepository extends JpaRepository<Hero, Long> {
	Optional<Hero> getByName(String name);
}

@Data
@Entity
class Hero {
	@Id
	@GeneratedValue
	private Long id;
	private String name;

	public Hero() {
	}

	public Hero(String name) {
		this.name = name;
	}
}