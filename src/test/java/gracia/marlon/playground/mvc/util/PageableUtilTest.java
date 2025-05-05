package gracia.marlon.playground.mvc.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;


import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import gracia.marlon.playground.mvc.dtos.PagedResponse;

public class PageableUtilTest {
	
	@Test
	public void pageableUtilInstance() {
		PageableUtil pageableUtil = new PageableUtil();
		assertTrue(pageableUtil instanceof PageableUtil);
	}

	@Test
	public void getPageableSuccessful() {
		final Sort sort = Sort.by(Sort.Order.desc("id"));
		Pageable pageable = PageableUtil.getPageable(0, 0, sort);

		assertEquals(0, pageable.getPageNumber());
		assertEquals(10, pageable.getPageSize());
		assertEquals(sort, pageable.getSort());
		
		pageable = PageableUtil.getPageable(null, null, sort);

		assertEquals(0, pageable.getPageNumber());
		assertEquals(10, pageable.getPageSize());
		
		pageable = PageableUtil.getPageable(20, 30, sort);

		assertEquals(19, pageable.getPageNumber());
		assertEquals(30, pageable.getPageSize());

	}
	
	@Test
	public void getPagedResponseSuccessful() {
		List<String> content = Arrays.asList("first", "second", "third");
        Pageable pageable = PageRequest.of(0, 10);
        Page<String> page = new PageImpl<String>(content, pageable, content.size());
        
        PagedResponse<String> pagedResponse = PageableUtil.getPagedResponse(page, content);
        
        assertEquals(1, pagedResponse.getPage());
        assertEquals(content, pagedResponse.getResults());
        assertEquals(1, pagedResponse.getTotalPages());
        assertEquals(3, pagedResponse.getTotalResults());
	}

}
