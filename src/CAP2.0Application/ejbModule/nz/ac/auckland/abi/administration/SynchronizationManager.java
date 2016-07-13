package nz.ac.auckland.abi.administration;

import java.util.Properties;

import javax.persistence.EntityManager;

import nz.ac.auckland.abi.businesslogic.InstanceBean;
import nz.ac.auckland.abi.businesslogic.SeriesBean;
import nz.ac.auckland.abi.businesslogic.StudyBean;
import nz.ac.auckland.abi.businesslogic.SubjectBean;
import nz.ac.auckland.abi.businesslogic.SystemNotificationBean;

public interface SynchronizationManager {
	public EntityManager getEntityManager();

	public SubjectBean getSubjectBean();

	public StudyBean getStudyBean();

	public SeriesBean getSeriesBean();

	public InstanceBean getInstanceBean();

	public Properties getPacsConnectionDetails();

	public SystemNotificationBean getSystemNotificationBean();

	public boolean cachePACSImageData();

	public boolean constrainModelsToPACSStudies();

	public void setCompletion();
	
	public String getTempDir();
}
