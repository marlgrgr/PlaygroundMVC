package gracia.marlon.playground.mvc.jms;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import gracia.marlon.playground.mvc.services.WebsocketService;
import gracia.marlon.playground.mvc.util.SharedConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessageListener {

	private final WebsocketService websocketService;

	@JmsListener(destination = SharedConstants.TOPIC_NOTIFICATION_NAME)
	public void receiveMessage(String message) {
		try {
			log.info("A request for broadcast notification to the user websocket was receive from the topic.");
			this.websocketService.broadcastMessage(message);
		} catch (Exception e) {
			log.error("An error occurred while broadcasting the message", e);
		}
	}
}
