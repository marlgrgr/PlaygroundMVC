package gracia.marlon.playground.mvc.dtos;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class PagedResponse<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	private long page;

	private List<T> results;

	private long totalPages;

	private long totalResults;

}
