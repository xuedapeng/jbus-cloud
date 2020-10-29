package cloud.jbus.logic.uiconfig.param;

import cloud.jbus.logic.BaseZLogicParam;

public class GetLastDataLogicParam extends BaseZLogicParam {

	private String projectId;
	private String childs; // yes/no

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getChilds() {
		return childs;
	}

	public void setChilds(String childs) {
		this.childs = childs;
	}
	
	
}
