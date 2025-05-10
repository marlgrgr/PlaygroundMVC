package gracia.marlon.playground.mvc.queue;

import gracia.marlon.playground.mvc.util.SharedConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class QueuePublisherNoSQSServiceImpl implements QueuePublisherService {

	private final AsyncConsumerService asyncConsumerService;
	
	@Override
	public void publish(String message, String queue) {
		if (SharedConstants.LOAD_MESSAGE.equalsIgnoreCase(message)) {
			asyncConsumerService.callAsync(message);
			log.info("A Load movies request message was request to be done async");
		}

	}

}
