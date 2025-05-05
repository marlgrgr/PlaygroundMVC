package gracia.marlon.playground.mvc.jms;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import gracia.marlon.playground.mvc.configuration.AbstractIntegrationBase;
import gracia.marlon.playground.mvc.services.MessageProducerService;
import gracia.marlon.playground.mvc.util.SharedConstants;

public class MessageListenerIT extends AbstractIntegrationBase{
	
	@Autowired
	private MessageProducerService messageProducerService;
	
	@Test
	void topicMessage() {

		assertDoesNotThrow(() -> {
			this.messageProducerService.publishMessage(SharedConstants.TOPIC_NOTIFICATION_NAME, SharedConstants.TOPIC_NOTIFICATION_NEW_MOVIE);
		});

	}

}
