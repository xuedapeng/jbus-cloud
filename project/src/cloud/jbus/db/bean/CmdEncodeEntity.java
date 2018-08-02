package cloud.jbus.db.bean;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "t_cmd_encode")
public class CmdEncodeEntity extends BaseZEntity {

	private Integer id;
	private Integer sensorId;
	private Integer cmdNo;
	private String cmdName;
	private String scriptText;
	private String paramSchema;
	private Integer includeCrc;
	
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Id
	public Integer getId() {
		return id;
	}

	public Integer getSensorId() {
		return sensorId;
	}

	public void setSensorId(Integer sensorId) {
		this.sensorId = sensorId;
	}

	public Integer getCmdNo() {
		return cmdNo;
	}

	public void setCmdNo(Integer cmdNo) {
		this.cmdNo = cmdNo;
	}

	public String getCmdName() {
		return cmdName;
	}

	public void setCmdName(String cmdName) {
		this.cmdName = cmdName;
	}

	public String getScriptText() {
		return scriptText;
	}

	public void setScriptText(String scriptText) {
		this.scriptText = scriptText;
	}

	public String getParamSchema() {
		return paramSchema;
	}

	public void setParamSchema(String paramSchema) {
		this.paramSchema = paramSchema;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getIncludeCrc() {
		return includeCrc;
	}

	public void setIncludeCrc(Integer includeCrc) {
		this.includeCrc = includeCrc;
	}
}
