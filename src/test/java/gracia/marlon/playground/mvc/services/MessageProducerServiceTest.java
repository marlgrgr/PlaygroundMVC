package gracia.marlon.playground.mvc.services;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.jms.core.JmsTemplate;

public class MessageProducerServiceTest {

	private final JmsTemplate jmsTemplate;

	private final MessageProducerService messageProducerService;

	public MessageProducerServiceTest() {
		this.jmsTemplate = Mockito.mock(JmsTemplate.class);
		this.messageProducerService = new MessageProducerServiceImpl(this.jmsTemplate);
	}

	@Test
	public void publishMessageSuccessful() {

		Mockito.doNothing().when(this.jmsTemplate).convertAndSend("destination", "message");

		this.messageProducerService.publishMessage("destination", "message");

		Mockito.verify(this.jmsTemplate).convertAndSend("destination", "message");
	}
}
