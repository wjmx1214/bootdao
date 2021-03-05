package com.boot.dao.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 多条件动态查询(分页包装类)
 * @param <T>
 * @author 2020-12-01 create wang.jia.le
 * @version 1.0.1
 */
public class Page<T> implements Serializable{
	
	private static final long serialVersionUID = 441767164793765757L;
	
	/**
	 * 面板显示多少个链接
	 */
	public int linkSize = 10;

	private int pageSize; 						//分页大小
	private int pageIndex; 						//分页索引
	private int count; 							//总记录数
	public List<T> data; 						//数据集合
	
	private int up;								//上一页
	private int next; 							//下一页
	private int allPage; 						//总分页数
	private List<String> pageList; 				//索引链接集合
	private PageSearch search; 					//查询参数对象(主要用于回显)
	private boolean pageIndexChange = false; 	//是否由于数据变动，分页索引被重置
	
	public Page(){
		this(1, 10);
	}
	
	public Page(int pageIndex, int pageSize) {
		this.pageIndex = pageIndex;
		this.pageSize = pageSize;
	}
	
	public Page(int pageIndex, int pageSize, int count, List<T> data) {
		this(pageIndex, pageSize);
		this.count = count;
		this.data = data;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public List<T> getData() {
		return data;
	}

	public void setData(List<T> data) {
		this.data = data;
	}
	
	public int getAllPage() {
		if(allPage == 0){
			allPage = count / pageSize;
			if(count % pageSize != 0){
				allPage++;
			}
		}
		return allPage;
	}
	
	public List<String> getPageList() {
		if(pageList == null){
			getAllPage();
			pageList = new ArrayList<>();
			if(allPage > linkSize){
				int startIndex = ((pageIndex - 1) / linkSize) * linkSize  + 1;
				for (int i = startIndex; i < linkSize + startIndex; i++) {
					if(i > allPage)
						break;
					pageList.add(i+"");
				}
			}else{
				for (int i = 1; i <= allPage; i++) {
					pageList.add(i+"");
				}
			}
		}
		return pageList;
	}
	
	public int getUp() {
		if(up == 0){
			up = (pageIndex > 1) ? pageIndex - 1 : pageIndex;
		}
		return up;
	}

	public int getNext() {
		if(next == 0){
			getAllPage();
			next = (pageIndex < allPage) ? pageIndex + 1 : pageIndex;
		}
		return next;
	}

	@SuppressWarnings("unchecked")
	public <S extends PageSearch> S getSearch() {
		return (S)search;
	}

	public void setSearch(PageSearch search) {
		this.search = search;
	}

	public boolean isPageIndexChange() {
		return pageIndexChange;
	}

	public void setPageIndexChange(boolean pageIndexChange) {
		this.pageIndexChange = pageIndexChange;
	}

}
