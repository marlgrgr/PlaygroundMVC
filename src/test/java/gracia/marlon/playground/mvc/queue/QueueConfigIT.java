package gracia.marlon.playground.mvc.queue;

import static org.junit.Assert.assertThrows;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.StandardEnvironment;

public class QueueConfigIT {

	@Test
	void sqsFailing() {
		try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
			
			ConfigurableEnvironment env = new StandardEnvironment();
			Map<String, Object> props = new HashMap<>();
			props.put("spring.cloud.aws.sqs.enabled", "true");
			env.getPropertySources().addFirst(new MapPropertySource("testProps", props));
			context.setEnvironment(env);

			context.register(QueueConfig.class);
			assertThrows(UnsatisfiedDependencyException.class, context::refresh);
		}
	}

}
