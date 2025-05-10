package gracia.marlon.playground.mvc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class PlaygroundMvcApplication {

	public static void main(String[] args) {
		SpringApplication.run(PlaygroundMvcApplication.class, args);
	}
}
