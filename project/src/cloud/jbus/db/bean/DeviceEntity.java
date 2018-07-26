package cloud.jbus.db.bean;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import cloud.jbus.common.constant.CrcConst;



@Entity
@Table(name = "t_device")
public class DeviceEntity extends BaseZEntity {

	private Integer id;
	private String deviceSn;
	private String secretKey;
	private String deviceName;
	private String longitude;
	private String latitude;
	private Integer crcMode = CrcConst.CRC_MODE_16_MODBUS;
	private Integer ownerId;
	private String memo;
	
	
	
	
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Id
	public Integer getId() {
		return id;
	}




	public String getDeviceSn() {
		return deviceSn;
	}




	public void setDeviceSn(String deviceSn) {
		this.deviceSn = deviceSn;
	}




	public String getDeviceName() {
		return deviceName;
	}




	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}




	public String getLongitude() {
		return longitude;
	}




	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}




	public String getLatitude() {
		return latitude;
	}




	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}




	public Integer getCrcMode() {
		return crcMode;
	}




	public void setCrcMode(Integer crcMode) {
		this.crcMode = crcMode;
	}




	public Integer getOwnerId() {
		return ownerId;
	}




	public void setOwnerId(Integer ownerId) {
		this.ownerId = ownerId;
	}




	public void setId(Integer id) {
		this.id = id;
	}




	public String getMemo() {
		return memo;
	}




	public void setMemo(String memo) {
		this.memo = memo;
	}




	public String getSecretKey() {
		return secretKey;
	}




	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	
}