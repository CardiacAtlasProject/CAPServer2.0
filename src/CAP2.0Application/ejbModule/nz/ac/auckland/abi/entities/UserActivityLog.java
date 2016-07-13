package nz.ac.auckland.abi.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "UserActivityLog")
public class UserActivityLog implements Serializable{
	private static final long serialVersionUID = 843562L;
	
	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY ) 
	@Column(name = "pkID")
	private Long pkID;
	
	@Column(name="activity")
	private String activity;
	
	@Column(name="description")
	private String description;
	
	@Column(name="quantity")
	private Double quantity;
	
	@Column(name="time")
	@Temporal(TemporalType.TIMESTAMP)
	private Date time;
	
	@JoinColumn(name = "username", referencedColumnName = "username")
    @ManyToOne(fetch = FetchType.LAZY)
	private Users myActivity;

	public UserActivityLog(){
		super();
	}
	
	public UserActivityLog(Users user, String event, String desc, double amount){
		myActivity = user;
		activity = event;
		quantity = new Double(amount);
		description = desc;
		if(desc==null)
			description="";
		if(description.length()>500){//Ensure it is not above the allocated size
			description = description.substring(0, 499);
		}
	}
	
	
	public Long getPkID() {
		return pkID;
	}

	public void setPkID(Long pkID) {
		this.pkID = pkID;
	}

	public String getActivity() {
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Double getQuantity() {
		return quantity;
	}

	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public Users getMyActivity() {
		return myActivity;
	}

	public void setMyActivity(Users myActivity) {
		this.myActivity = myActivity;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((activity == null) ? 0 : activity.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((pkID == null) ? 0 : pkID.hashCode());
		result = prime * result + ((quantity == null) ? 0 : quantity.hashCode());
		result = prime * result + ((time == null) ? 0 : time.hashCode());
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
		UserActivityLog other = (UserActivityLog) obj;
		if (activity == null) {
			if (other.activity != null)
				return false;
		} else if (!activity.equals(other.activity))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (pkID == null) {
			if (other.pkID != null)
				return false;
		} else if (!pkID.equals(other.pkID))
			return false;
		if (quantity == null) {
			if (other.quantity != null)
				return false;
		} else if (!quantity.equals(other.quantity))
			return false;
		if (time == null) {
			if (other.time != null)
				return false;
		} else if (!time.equals(other.time))
			return false;
		return true;
	}

	
}
