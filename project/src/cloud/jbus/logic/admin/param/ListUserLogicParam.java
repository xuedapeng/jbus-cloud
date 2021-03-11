package cloud.jbus.logic.admin.param;

import cloud.jbus.logic.BaseZLogicParam;
import cloud.jbus.logic.device.param.IPrivilegedParam;
import cloud.jbus.logic.share.annotation.Privilege;

@Privilege("admin")
public class ListUserLogicParam extends BaseZLogicParam implements IPrivilegedParam {

	public ListUserLogicParam() {
		
	}


	private String page;
	private String pageSize;
	
	private String keyword;

	
	
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

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	@Override
	public String getDeviceId() {

		return "0";
	}

	
}
