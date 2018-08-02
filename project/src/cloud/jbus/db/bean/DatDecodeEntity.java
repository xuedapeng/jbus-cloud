package cloud.jbus.db.bean;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "t_dat_decode")
public class DatDecodeEntity extends BaseZEntity {

	private Integer id;
	private Integer deviceId;
	private String scriptText;
	private String resultSchema;
	private Integer includeCrc;

	
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Id
	public Integer getId() {
		return id;
	}


	public Integer getDeviceId() {
		return deviceId;
	}


	public void setDeviceId(Integer deviceId) {
		this.deviceId = deviceId;
	}


	public String getScriptText() {
		return scriptText;
	}


	public void setScriptText(String scriptText) {
		this.scriptText = scriptText;
	}


	public String getResultSchema() {
		return resultSchema;
	}


	public void setResultSchema(String resultSchema) {
		this.resultSchema = resultSchema;
	}


	public Integer getIncludeCrc() {
		return includeCrc;
	}


	public void setIncludeCrc(Integer includeCrc) {
		this.includeCrc = includeCrc;
	}


	public void setId(Integer id) {
		this.id = id;
	}
	
}