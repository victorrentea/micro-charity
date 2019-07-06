package com.example.charityclient;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@SpringBootApplication
@EnableBinding(Source.class)
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
@Slf4j
class CharityController {
	private final RestTemplate restTemplate;
	private final Source source;

    @GetMapping("charity")
    public List<String> getAllCharityNames() {
    	log.info("Sending GET");
    	return restTemplate.exchange("http://charity-service/charity", HttpMethod.GET,
				null, new ParameterizedTypeReference<Resources<CharityDto>>() {
				}).getBody()
			.getContent()
			.stream()
			.map(CharityDto::getName)
			.collect(Collectors.toList());
	}

	@Data
	static class CreateCharityForm {
    	private String name, heroName;
	}


    @PostMapping("charity")
	public DeferredResult<String> createCharity(@RequestBody CreateCharityForm form) {
    	log.info("Sending POST");
//    	source.output().send(MessageBuilder.withPayload(name).build());
		DeferredResult<String> output = new DeferredResult<>();


		CompletableFuture.supplyAsync(() -> restTemplate.getForObject("http://hero-service/hero/id?name=" + form.getHeroName(), Long.class))
				.thenApply(heroId -> restTemplate.postForEntity("http://charity-service/charity", new CharityDto(form.name, heroId), String.class)
                	.getHeaders()
					.getLocation()
					.getPath())
			.thenApply(path -> path.substring(path.lastIndexOf("/") + 1))
			.thenAccept(output::setResult)
			.thenRun(() -> log.info("Response sent"));
		log.info("Thread freed");
        return output;
	}
}

@Data
class CharityDto {
	private Long id;
	private String name;
	private Long heroId;

    public CharityDto() {
    }

    public CharityDto(String name, Long heroId) {
        this.name = name;
		this.heroId = heroId;
	}
}

