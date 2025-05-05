package gracia.marlon.playground.mvc.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import gracia.marlon.playground.mvc.model.MovieReview;

@Repository
public interface MovieReviewRepository extends MongoRepository<MovieReview, String> {

	Page<MovieReview> findAll(Pageable pageable);

	Page<MovieReview> findByMovieId(Long id, Pageable pageable);
}
