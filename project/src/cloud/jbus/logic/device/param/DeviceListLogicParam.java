package cloud.jbus.logic.device.param;

import cloud.jbus.logic.BaseZLogicParam;

public class DeviceListLogicParam extends BaseZLogicParam {

	public DeviceListLogicParam() {
		
	}

	private String page;
	private String pageSize;
	
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
