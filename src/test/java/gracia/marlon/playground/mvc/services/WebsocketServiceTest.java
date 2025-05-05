package gracia.marlon.playground.mvc.services;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.socket.WebSocketSession;

public class WebsocketServiceTest {

	private final WebsocketService websocketService;

	public WebsocketServiceTest() {
		this.websocketService = new WebsocketServiceImpl();
	}

	@Test
	public void broadcastMessageSuccessful() throws IOException {
		WebSocketSession session1 = Mockito.mock(WebSocketSession.class);
		WebSocketSession session2 = Mockito.mock(WebSocketSession.class);
		WebSocketSession session3 = Mockito.mock(WebSocketSession.class);

		Mockito.when(session1.isOpen()).thenReturn(true);
		Mockito.doNothing().when(session1).sendMessage(Mockito.any());
		Mockito.when(session2.isOpen()).thenReturn(false);

		this.websocketService.addSession(session1);
		this.websocketService.addSession(session2);
		this.websocketService.addSession(session3);
		this.websocketService.removeSession(session3);
		this.websocketService.broadcastMessage("my message");

		Mockito.verify(session1, Mockito.times(1)).isOpen();
		Mockito.verify(session2, Mockito.times(1)).isOpen();
		Mockito.verify(session3, Mockito.never()).isOpen();

		Mockito.verify(session1, Mockito.times(1)).sendMessage(Mockito.any());
		Mockito.verify(session2, Mockito.never()).sendMessage(Mockito.any());
		Mockito.verify(session3, Mockito.never()).sendMessage(Mockito.any());

	}

}
