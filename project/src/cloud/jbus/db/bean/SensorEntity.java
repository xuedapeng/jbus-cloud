package cloud.jbus.db.bean;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "t_sensor")
public class SensorEntity extends BaseZEntity {

	private Integer id;
	private Integer deviceId;
	private Integer sensorNo;
	private String sensorName;
	private String memo;
	
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

	public Integer getSensorNo() {
		return sensorNo;
	}

	public void setSensorNo(Integer sensorNo) {
		this.sensorNo = sensorNo;
	}

	public String getSensorName() {
		return sensorName;
	}

	public void setSensorName(String sensorName) {
		this.sensorName = sensorName;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
}