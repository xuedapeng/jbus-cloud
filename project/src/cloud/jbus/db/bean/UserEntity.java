package cloud.jbus.db.bean;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;



@Entity
@Table(name = "t_user")
public class UserEntity extends BaseZEntity {

	private Integer id;
	private String account;
	private String password;
	private String secretId;
	private String secretKey;
	private String nickName;
	private String email;
	private Integer status;
	private Integer sysAdmin;
	private Date createTime;
	
	
	
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Id
	public Integer getId() {
		return id;
	}



	public String getAccount() {
		return account;
	}



	public void setAccount(String account) {
		this.account = account;
	}



	public String getPassword() {
		return password;
	}



	public void setPassword(String password) {
		this.password = password;
	}



	public String getSecretId() {
		return secretId;
	}



	public void setSecretId(String secretId) {
		this.secretId = secretId;
	}



	public String getSecretKey() {
		return secretKey;
	}



	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}



	public String getNickName() {
		return nickName;
	}



	public void setNickName(String nickName) {
		this.nickName = nickName;
	}



	public String getEmail() {
		return email;
	}



	public void setEmail(String email) {
		this.email = email;
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



	public Integer getSysAdmin() {
		return sysAdmin;
	}



	public void setSysAdmin(Integer sysAdmin) {
		this.sysAdmin = sysAdmin;
	}



	public Date getCreateTime() {
		return createTime;
	}



	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	
	
}