package com.example.heroservice;

import com.netflix.discovery.converters.Auto;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.persistence.Entity;
import javax.persistence.EntityNotFoundException;
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

	@GetMapping
	public Hero findByName(@RequestParam String name) {
		return repo.findByName(name).orElseThrow(EntityNotFoundException::new);
	}

	@PostMapping
	public Long create(@RequestBody Hero hero) { // NEVER get/send Entity instances over REST in Production
		repo.save(hero);
		return hero.getId();
	}
}

interface HeroRepository extends JpaRepository<Hero, Long> {
	Optional<Hero> findByName(String name);
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