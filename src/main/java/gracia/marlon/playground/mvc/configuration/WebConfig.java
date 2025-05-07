package gracia.marlon.playground.mvc.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	private final String[] allowedOrigins;

	public WebConfig(Environment env) {
		this.allowedOrigins = env.getProperty("cors.allowed-origins", "").split(",");
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**").allowedOrigins(this.allowedOrigins).allowedMethods("*")
				.allowedHeaders("Content-Type", "Authorization").allowCredentials(true);
	}
}
