package gracia.marlon.playground.mvc.util;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import gracia.marlon.playground.mvc.dtos.PagedResponse;

public class PageableUtil {

	private final static int PAGE_BASE = 1;

	private final static int PAGE_MIN_SIZE = 1;

	private final static int PAGE_DEFAULT_SIZE = 10;

	public static Pageable getPageable(Integer page, Integer pageSize, Sort sort) {
		int pageInt = page == null || page < PAGE_BASE ? PAGE_BASE : page;
		int pageSizeInt = pageSize == null || pageSize < PAGE_MIN_SIZE ? PAGE_DEFAULT_SIZE : pageSize;
		Pageable pageable = PageRequest.of(pageInt - PAGE_BASE, pageSizeInt, sort);

		return pageable;
	}

	public static <A, B> PagedResponse<B> getPagedResponse(Page<A> page, List<B> response) {
		PagedResponse<B> pagedResponse = new PagedResponse<B>();
		pagedResponse.setPage(page.getNumber() + PAGE_BASE);
		pagedResponse.setTotalPages(page.getTotalPages());
		pagedResponse.setTotalResults(page.getTotalElements());
		pagedResponse.setResults(response);

		return pagedResponse;
	}

}
