package nz.ac.auckland.abi.businesslogic;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import nz.ac.auckland.abi.entities.SystemNotification;

/**
 * Session Bean implementation class SystemNotificationBean
 */
@Stateless
@LocalBean
public class SystemNotificationBean implements SystemNotificationBeanRemote {

	@PersistenceContext(unitName = "CAPDS")
	private EntityManager entityManager;
	
    /**
     * Default constructor. 
     */
    public SystemNotificationBean() {
        
    }
    
	public void log(String evt, String msg){
		SystemNotification notification = new SystemNotification(evt,msg);
		entityManager.persist(notification);
	}
	
	public List<SystemNotification> getNotifications(){
		return entityManager.createQuery("SELECT p FROM Notifications p", SystemNotification.class).getResultList();
	}
	
	public void purgeNotifications(){
		entityManager.createQuery("DELETE FROM Notifications").executeUpdate();
	}

	public void purgeNotification(String id){
		SystemNotification record = entityManager.find(SystemNotification.class, id);
		if(record!=null){
			entityManager.remove(record);
			entityManager.flush();
		}
	}
}
