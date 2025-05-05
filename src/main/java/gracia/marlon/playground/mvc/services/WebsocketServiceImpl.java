package gracia.marlon.playground.mvc.services;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class WebsocketServiceImpl implements WebsocketService {

	private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

	@Override
	public void addSession(WebSocketSession session) {
		this.sessions.add(session);
	}

	@Override
	public void removeSession(WebSocketSession session) {
		this.sessions.remove(session);
	}

	@Override
	public void broadcastMessage(String message) throws IOException {
		int messagesSent = 0;

		for (WebSocketSession session : sessions) {
			if (session.isOpen()) {
				session.sendMessage(new TextMessage(message));
				messagesSent++;
			}
		}

		log.info("The broadcast message was sent to " + messagesSent + " open websocket sessions");
	}

}
