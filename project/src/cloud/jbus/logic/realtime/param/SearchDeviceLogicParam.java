package cloud.jbus.logic.realtime.param;

import cloud.jbus.logic.BaseZLogicParam;

public class SearchDeviceLogicParam extends BaseZLogicParam {

	public SearchDeviceLogicParam() {
		
	}
	
	private String filter;
	private String page;
	private String pageSize;

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public String getPageSize() {
		return pageSize;
	}

	public void setPageSize(String pageSize) {
		this.pageSize = pageSize;
	}
}
