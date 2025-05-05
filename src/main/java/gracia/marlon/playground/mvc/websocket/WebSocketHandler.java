package gracia.marlon.playground.mvc.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import gracia.marlon.playground.mvc.services.WebsocketService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WebSocketHandler extends TextWebSocketHandler {

	private final WebsocketService websocketService;

	@Override
	public void afterConnectionEstablished(WebSocketSession session) {
		this.websocketService.addSession(session);
	}

	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message) {
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
		this.websocketService.removeSession(session);
	}

}
