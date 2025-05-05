package gracia.marlon.playground.mvc.queue;

import org.springframework.stereotype.Service;

import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class QueuePublisherServiceImpl implements QueuePublisherService {

	private final SqsTemplate sqsTemplate;

	@Override
	public void publish(String message, String queue) {
		sqsTemplate.send(to -> to.queue(queue).payload(message));
		log.info("A Load movies request message was sent to the queue");
	}

}
