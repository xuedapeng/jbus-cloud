package cloud.jbus.logic.device.param;

import cloud.jbus.logic.BaseZLogicParam;

public class DeviceListLogicParam extends BaseZLogicParam {

	public DeviceListLogicParam() {
		
	}

	private String page;
	private String pageSize;
	private String category;
	
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
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	
	
	
}
