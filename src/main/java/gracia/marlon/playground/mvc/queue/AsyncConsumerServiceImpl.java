package gracia.marlon.playground.mvc.queue;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import gracia.marlon.playground.mvc.services.LoadMoviesService;
import gracia.marlon.playground.mvc.util.SharedConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AsyncConsumerServiceImpl implements AsyncConsumerService{

	private final LoadMoviesService loadMoviesService;
	
	@Override
	@Async
	public void callAsync(String message) {
		if (SharedConstants.LOAD_MESSAGE.equalsIgnoreCase(message)) {
			log.info("A Load movies request message was receive asynchronously");
			
			loadMoviesService.loadMovies();
		}
	}

}
