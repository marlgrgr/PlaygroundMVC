package gracia.marlon.playground.mvc.dtos;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class MovieDetailsDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private long id;

	@JsonProperty("genre_ids")
	private List<Integer> genreIds;

	private List<String> genres;

	@JsonProperty("original_language")
	private String originalLanguage;

	@JsonProperty("original_title")
	private String originalTitle;

	private String overview;

	private Double popularity;

	@JsonProperty("poster_path")
	private String posterPath;

	@JsonProperty("release_date")
	private String releaseDate;

	private String title;

	@JsonProperty("vote_average")
	private Double voteAverage;

	@JsonProperty("vote_count")
	private Double voteCount;

	@JsonProperty("create_on")
	private Date createOn;

}
