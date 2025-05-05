package gracia.marlon.playground.mvc.websocket;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import gracia.marlon.playground.mvc.configuration.AbstractIntegrationBase;

public class WebsocketHandlerIT extends AbstractIntegrationBase {

	@LocalServerPort
	private int port;

	@Test
	public void testWebSocketEcho() throws Exception {

		StandardWebSocketClient client = new StandardWebSocketClient();

		CountDownLatch latch = new CountDownLatch(1);

		TextWebSocketHandler handler = new TextWebSocketHandler() {
			@Override
			public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {

			}

			@Override
			public void afterConnectionEstablished(WebSocketSession session) throws Exception {
				session.sendMessage(new TextMessage("Websocket message"));

				session.close();
				latch.countDown();
			}
		};

		assertDoesNotThrow(() -> {
			WebSocketConnectionManager manager = new WebSocketConnectionManager(client, handler,
					"ws://localhost:" + port + "/ws");
			manager.start();

			assertTrue(latch.await(5, TimeUnit.SECONDS));
		});
	}
}
