package gracia.marlon.playground.mvc.services;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import gracia.marlon.playground.mvc.dtos.MovieDetailsDTO;
import gracia.marlon.playground.mvc.dtos.MovieDetailsResponseDTO;
import gracia.marlon.playground.mvc.mapper.MovieDetailsMapper;
import gracia.marlon.playground.mvc.model.Movie;
import gracia.marlon.playground.mvc.repository.MovieRepository;
import gracia.marlon.playground.mvc.util.SharedConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SaveMoviesServiceImpl implements SaveMoviesService {

	private final MovieRepository movieRepository;

	private final CacheService cacheService;

	private final MessageProducerService messageProducerService;

	private final MovieDetailsMapper movieDetailsMapper;

	@Override
	public void saveMovieList(MovieDetailsResponseDTO movieDetailsResponseDTO) {
		if (!this.validMovieDetailsResponseDTO(movieDetailsResponseDTO)) {
			return;
		}

		try {

			final Date createdOn = new Date();
			movieDetailsResponseDTO.getResults().stream().forEach(x -> x.setCreateOn(createdOn));

			this.saveMovies(movieDetailsResponseDTO.getResults());

			this.evictMovieCaches();

			this.messageProducerService.publishMessage(SharedConstants.TOPIC_NOTIFICATION_NAME,
					SharedConstants.TOPIC_NOTIFICATION_NEW_MOVIE);
			
			log.info("The movie list was succesfully persisted on the DB");

		} catch (Exception e) {
			log.error("An error occurred while saving the movie list", e);
		}

	}

	private boolean validMovieDetailsResponseDTO(MovieDetailsResponseDTO movieDetailsResponseDTO) {
		return movieDetailsResponseDTO != null && movieDetailsResponseDTO.getResults() != null
				&& !movieDetailsResponseDTO.getResults().isEmpty();
	}

	private void saveMovies(List<MovieDetailsDTO> movieDetailsDTOList) {
		final List<Movie> movieList = movieDetailsDTOList.stream().map(this.movieDetailsMapper::toEntity)
				.collect(Collectors.toList());
		this.movieRepository.saveAll(movieList);
	}

	private void evictMovieCaches() {
		this.cacheService.evictCache("MovieService_getMovieList");
	}

}
