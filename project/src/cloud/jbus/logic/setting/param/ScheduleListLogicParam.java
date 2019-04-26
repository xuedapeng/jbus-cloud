package cloud.jbus.logic.setting.param;

import cloud.jbus.logic.BaseZLogicParam;
import cloud.jbus.logic.device.param.IPageParam;
import cloud.jbus.logic.device.param.IPrivilegedParam;
import cloud.jbus.logic.share.annotation.Privilege;

@Privilege("view")
public class ScheduleListLogicParam extends BaseZLogicParam implements IPrivilegedParam,IPageParam {

	public ScheduleListLogicParam() {
		
	}

	private String page;
	private String pageSize;
	private String deviceId;
	
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

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	@Override
	public String getDeviceId() {
		return deviceId;
	}

	
	
}
