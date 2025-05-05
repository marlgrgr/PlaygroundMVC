package gracia.marlon.playground.mvc.services;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageProducerServiceImpl implements MessageProducerService {

	private final JmsTemplate jmsTemplate;

	@Override
	public void publishMessage(String destination, String message) {
		jmsTemplate.convertAndSend(destination, message);
		
		log.info("A request for broadcast notification to the user websocket was sent to the topic.");
	}

}
