package gracia.marlon.playground.mvc.dtos;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class MovieDetailsResponseDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private long page;

	private List<MovieDetailsDTO> results;

	@JsonProperty("total_pages")
	private long totalPages;

	@JsonProperty("total_results")
	private long totalResults;
}
