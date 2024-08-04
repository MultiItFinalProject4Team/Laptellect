package com.multi.laptellect.common.model;

import lombok.Data;

@Data
public class PageDTO {
	private int start;
	private int end;
	private int page;

	public void setStartEnd(int page) {
		this.page = page;
		this.start = (page - 1) * 10;
		this.end = 10;
	}
	
}
