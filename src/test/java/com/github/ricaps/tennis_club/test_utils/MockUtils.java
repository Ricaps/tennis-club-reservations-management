package com.github.ricaps.tennis_club.test_utils;

import org.mockito.Mockito;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class MockUtils {

	private MockUtils() {
		super();
	}

	public static Pageable mockPageable(int pageNumber, int pageSize, Sort sortBy) {
		Pageable pageableMock = Mockito.mock(Pageable.class);
		Mockito.when(pageableMock.getPageNumber()).thenReturn(pageNumber);
		Mockito.when(pageableMock.getPageSize()).thenReturn(pageSize);
		Mockito.when(pageableMock.getSort()).thenReturn(sortBy);
		return pageableMock;
	}

}
