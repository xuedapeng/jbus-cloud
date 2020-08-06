package cloud.jbus.logic.uiconfig.param;

import cloud.jbus.logic.BaseZLogicParam;

public class AddUiconfigLogicParam extends BaseZLogicParam {

	private String pid;
	private String title;
	private String cover;
	private String sort;
	
	
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getCover() {
		return cover;
	}
	public void setCover(String cover) {
		this.cover = cover;
	}
	public String getSort() {
		return sort;
	}
	public void setSort(String sort) {
		this.sort = sort;
	}
	
	
	

}
