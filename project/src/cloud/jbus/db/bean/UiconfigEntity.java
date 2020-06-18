package cloud.jbus.db.bean;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "t_ui_config")
public class UiconfigEntity extends BaseZEntity {

	private Integer id;
	private String title;
	private String deviceSnList;
	private String cover;
	private String elements;
	private String html;
	private Date htmlTime;
	private Integer ownerId;
	private Integer status;
	
	
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Id
	public Integer getId() {
		return id;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public String getDeviceSnList() {
		return deviceSnList;
	}


	public void setDeviceSnList(String deviceSnList) {
		this.deviceSnList = deviceSnList;
	}


	public String getCover() {
		return cover;
	}


	public void setCover(String cover) {
		this.cover = cover;
	}


	public String getElements() {
		return elements;
	}


	public void setElements(String elements) {
		this.elements = elements;
	}


	public String getHtml() {
		return html;
	}


	public void setHtml(String html) {
		this.html = html;
	}


	public Date getHtmlTime() {
		return htmlTime;
	}


	public void setHtmlTime(Date htmlTime) {
		this.htmlTime = htmlTime;
	}


	public Integer getOwnerId() {
		return ownerId;
	}


	public void setOwnerId(Integer ownerId) {
		this.ownerId = ownerId;
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
	
	
}