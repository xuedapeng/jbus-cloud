package cloud.jbus.logic.uiconfig.param;

import cloud.jbus.logic.BaseZLogicParam;

public class GetUiconfigsOfPidLogicParam extends BaseZLogicParam {

	private String projectId;

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}
}