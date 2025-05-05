package gracia.marlon.playground.mvc.model;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "movie")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Movie {

	@Id
	private Long id;

	private List<Integer> genreIds;

	private List<String> genres;

	private String originalLanguage;

	private String originalTitle;

	private String overview;

	private Double popularity;

	private String posterPath;

	private String releaseDate;

	private String title;

	private Double voteAverage;

	private Double voteCount;

	private Date createOn;

}
