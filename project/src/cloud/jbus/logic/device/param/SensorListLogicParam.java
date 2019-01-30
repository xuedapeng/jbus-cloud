package cloud.jbus.logic.device.param;


import cloud.jbus.logic.BaseZLogicParam;
import cloud.jbus.logic.share.annotation.Privilege;

@Privilege("view")
public class SensorListLogicParam extends BaseZLogicParam  implements IPrivilegedParam,IPageParam {

	public SensorListLogicParam() {
		
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
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
}
