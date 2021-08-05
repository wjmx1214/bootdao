package com.boot.dao.api;

/**
 * 排序
 * @author 2021-07-31 create wang.jia.le
 * @version 1.0.7
 */
public enum Sort {
	
	NOT		(""),
	ASC		("asc"),
	DESC	("desc");

	public String sort;
	Sort(String sort){
		this.sort = sort;
	}
	
	@Override
	public String toString() {
        return sort;
    }

}
