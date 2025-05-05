package gracia.marlon.playground.mvc.services;

public interface MessageProducerService {

	void publishMessage(String destination, String message);

}
