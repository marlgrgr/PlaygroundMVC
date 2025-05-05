package gracia.marlon.playground.mvc.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "movie_review")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MovieReview {

	@Id
	private String id;

	private String review;

	private Double score;

	private Long movieId;

	private Date createOn;

}
