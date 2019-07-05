package com.example.charityservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
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