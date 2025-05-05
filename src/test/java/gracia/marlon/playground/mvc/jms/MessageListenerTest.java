package gracia.marlon.playground.mvc.jms;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import gracia.marlon.playground.mvc.services.WebsocketService;

public class MessageListenerTest {

	private final WebsocketService websocketService;

	private final MessageListener messageListener;

	public MessageListenerTest() {
		this.websocketService = Mockito.mock(WebsocketService.class);
		this.messageListener = new MessageListener(this.websocketService);
	}

	@Test
	public void receiveMessageException() throws IOException {

		Mockito.doThrow(new RuntimeException()).when(this.websocketService).broadcastMessage(Mockito.anyString());

		assertDoesNotThrow(() -> {
			this.messageListener.receiveMessage("message");
		});
	}

}
