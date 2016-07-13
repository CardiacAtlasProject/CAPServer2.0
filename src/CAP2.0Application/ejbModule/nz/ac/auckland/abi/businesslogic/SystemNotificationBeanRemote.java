package nz.ac.auckland.abi.businesslogic;

import java.util.List;

import javax.ejb.Remote;

import nz.ac.auckland.abi.entities.SystemNotification;

@Remote
public interface SystemNotificationBeanRemote {

	public void log(String evt, String msg);

	public List<SystemNotification> getNotifications();

	public void purgeNotifications();

	public void purgeNotification(String id);

}
