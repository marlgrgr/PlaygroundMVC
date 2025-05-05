package gracia.marlon.playground.mvc.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import gracia.marlon.playground.mvc.model.Movie;

@Repository
public interface MovieRepository extends MongoRepository<Movie, Long> {

	Page<Movie> findAll(Pageable pageable);

}
