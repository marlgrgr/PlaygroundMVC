package gracia.marlon.playground.mvc.configuration;

import static org.junit.Assert.assertThrows;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.StandardEnvironment;

public class CacheConfigurationFailingIT {

	@Test
	void redissonClientFailing() {
		try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {

			ConfigurableEnvironment env = new StandardEnvironment();
			Map<String, Object> props = new HashMap<>();
			props.put("redis.config.path", "nonExistingPath.yaml");
			env.getPropertySources().addFirst(new MapPropertySource("testProps", props));
			context.setEnvironment(env);

			context.register(CacheConfiguration.class);
			assertThrows(UnsatisfiedDependencyException.class, context::refresh);
		}
	}
}
