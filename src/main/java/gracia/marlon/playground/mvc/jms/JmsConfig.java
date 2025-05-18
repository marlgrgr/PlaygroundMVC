package gracia.marlon.playground.mvc.jms;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;

import jakarta.jms.ConnectionFactory;

@Configuration
@EnableJms
public class JmsConfig {

	private final String activemqUrl;

	public JmsConfig(Environment env) {
		this.activemqUrl = env.getProperty("spring.activemq.broker-url", "");
	}

	@Bean
	ConnectionFactory activeMQconnectionFactory() {
		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(activemqUrl);
		return factory;
	}

	@Bean
	JmsTemplate jmsTemplate(ConnectionFactory connectionFactory) {
		JmsTemplate template = new JmsTemplate(connectionFactory);
		template.setPubSubDomain(true);
		return template;
	}

	@Bean
	DefaultJmsListenerContainerFactory topicListenerFactory(ConnectionFactory connectionFactory) {
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		factory.setConnectionFactory(connectionFactory);
		factory.setPubSubDomain(true);
		return factory;
	}
	
}
