package com.example.charityservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class CharityServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CharityServiceApplication.class, args);
	}

}

@RestController
class Hello {
	@GetMapping("hello")
	public String hello() {
		return "Hello!";
	}
}
