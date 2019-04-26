package cloud.jbus.db.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "t_schedule")
public class ScheduleEntity extends BaseZEntity {

	private Integer id;
	private Integer deviceId;
	private String deviceSn;
	private String cmdHex;
	private String datPtn;
	private Integer interval;
	private Integer status;
	
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

	public String getDeviceSn() {
		return deviceSn;
	}

	public void setDeviceSn(String deviceSn) {
		this.deviceSn = deviceSn;
	}

	public String getCmdHex() {
		return cmdHex;
	}

	public void setCmdHex(String cmdHex) {
		this.cmdHex = cmdHex;
	}

	public String getDatPtn() {
		return datPtn;
	}

	public void setDatPtn(String datPtn) {
		this.datPtn = datPtn;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name="`interval`")
	public Integer getInterval() {
		return interval;
	}

	public void setInterval(Integer interval) {
		this.interval = interval;
	}

}