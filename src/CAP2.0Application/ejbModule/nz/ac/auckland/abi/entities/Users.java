package nz.ac.auckland.abi.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "Users")
public class Users implements Serializable{
	private static final long serialVersionUID = 843562L;
	
	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY ) 
	@Column(name = "pkID")
	private Long pkID;
	
	@Column(name="username")
	private String userID;
	
	@Column(name="name")
	private String userName;
	
	@Column(name="passwd")
	private String password;
	
	@Column(name="userdescription")
	private String description;
	
	@Column(name="lastlogin")
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastLogin;
	
	@Column(name="lastpasswordchange")
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastPasswordChange;
	
	
	@OneToMany(mappedBy = "myRole", fetch = FetchType.LAZY)
	private Set<UserRoles> roles;
	
	@OneToMany(mappedBy = "myActivity", fetch = FetchType.LAZY)
	private Set<UserActivityLog> activities;

	public Users(){
		super();
	}
	
	public Long getPkID() {
		return pkID;
	}

	public void setPkID(Long pkID) {
		this.pkID = pkID;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	public Date getLastPasswordChange() {
		return lastPasswordChange;
	}

	public void setLastPasswordChange(Date lastPasswordChange) {
		this.lastPasswordChange = lastPasswordChange;
	}

	public Set<UserRoles> getRoles() {
		return roles;
	}

	public void setRoles(Set<UserRoles> roles) {
		this.roles = roles;
	}

	public Set<UserActivityLog> getActivities() {
		return activities;
	}

	public void setActivities(Set<UserActivityLog> activities) {
		this.activities = activities;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((pkID == null) ? 0 : pkID.hashCode());
		result = prime * result + ((userID == null) ? 0 : userID.hashCode());
		result = prime * result + ((userName == null) ? 0 : userName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Users other = (Users) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (pkID == null) {
			if (other.pkID != null)
				return false;
		} else if (!pkID.equals(other.pkID))
			return false;
		if (userID == null) {
			if (other.userID != null)
				return false;
		} else if (!userID.equals(other.userID))
			return false;
		if (userName == null) {
			if (other.userName != null)
				return false;
		} else if (!userName.equals(other.userName))
			return false;
		return true;
	}
		

}
