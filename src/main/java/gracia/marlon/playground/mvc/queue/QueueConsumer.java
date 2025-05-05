package gracia.marlon.playground.mvc.queue;

import org.springframework.transaction.annotation.Transactional;

import gracia.marlon.playground.mvc.services.LoadMoviesService;
import gracia.marlon.playground.mvc.util.SharedConstants;
import io.awspring.cloud.sqs.annotation.SqsListener;
import io.awspring.cloud.sqs.listener.acknowledgement.Acknowledgement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class QueueConsumer {

	private final LoadMoviesService loadMoviesService;

	@SqsListener(value = "${sqs.queue.loadMovies.url}", acknowledgementMode = "MANUAL")
	@Transactional
	public void listenerRfaUpdate(String messageBody, Acknowledgement acknowledgement) {
		try {
			if (SharedConstants.LOAD_MESSAGE.equalsIgnoreCase(messageBody)) {
				log.info("A Load movies request message was receive from the queue");
				
				loadMoviesService.loadMovies();
			}
		} finally {
			acknowledgement.acknowledge();
		}
	}

}
