package gracia.marlon.playground.mvc.services;

import java.io.IOException;

import org.springframework.web.socket.WebSocketSession;

public interface WebsocketService {

	void addSession(WebSocketSession session);

	void removeSession(WebSocketSession session);

	void broadcastMessage(String message) throws IOException;

}
