package gracia.marlon.playground.mvc.dtos;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class MovieDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private long id;

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

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "UTC")
	private Date createOn;

}
