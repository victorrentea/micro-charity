package com.example.charityclient;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
public class CharityClientApplication {

	@Bean
	@LoadBalanced
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	public static void main(String[] args) {
		SpringApplication.run(CharityClientApplication.class, args);
	}

}

@RequiredArgsConstructor
@RestController
class CharityGateway {
	private final RestTemplate restTemplate;

	@GetMapping("charity-names")
	public List<String> getCharityNames() {
		return restTemplate.exchange("http://charity-service/charity", HttpMethod.GET, null,
				new ParameterizedTypeReference<Resources<CharityDto>>() {
				}
				)
			.getBody()
			.getContent()
			.stream()
			.map(CharityDto::getName)
			.collect(Collectors.toList());
	}
}

@Data
class CharityDto {
	private String name;
}