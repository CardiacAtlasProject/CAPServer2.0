package nz.ac.auckland.abi.businesslogic;

import java.util.Vector;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import nz.ac.auckland.abi.entities.Instance;
import nz.ac.auckland.abi.entities.InstancePK;
import nz.ac.auckland.abi.entities.Series;
import nz.ac.auckland.abi.entities.SeriesPK;
import nz.ac.auckland.abi.entities.Study;
import nz.ac.auckland.abi.entities.StudyPK;
import nz.ac.auckland.abi.entities.Subject;

/**
 * Session Bean implementation class SubjectBean
 */
@Stateless
@LocalBean
public class SubjectBean {

	@PersistenceContext(unitName = "CAPDS")
	private EntityManager entityManager;

	/**
	 * Default constructor.
	 */
	public SubjectBean() {
		super();
	}

	public Subject addSubject(Subject subj) {
		if (entityManager.find(Subject.class, subj.getId()) == null)
			entityManager.persist(subj);
		else {
			entityManager.merge(subj);
			entityManager.refresh(subj);
		}
		return subj;
	}

	public void removeSubject(String subjectID) {
		Subject del = entityManager.find(Subject.class, subjectID);
		if (del != null) {
			entityManager.refresh(del);
			entityManager.flush();
		}
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void addSubject(Subject subj, Vector<Study> studies,
			Vector<Series> series, Vector<Instance> instances) {
		// If a Subject is specified but is not available in the database then
		// create
		if (subj != null
				&& entityManager.find(Subject.class, subj.getId()) == null) {
			entityManager.persist(subj);
			// Since the subject is new all else will be new
			for (Study study : studies) {
				entityManager.persist(study);
			}
			for (Series serie : series) {
				entityManager.persist(serie);
			}
			for (Instance inst : instances) {
				entityManager.persist(inst);
			}
		} else {// Subject is present a new study/series/instances has been
				// added
				// Check if the study is present and add if it is not there
			if (studies != null)
				for (Study study : studies) {
					StudyPK key = new StudyPK();
					key.setPatientID(study.getSubject());
					key.setStudyID(study.getStudyID());
					if (entityManager.find(Study.class, key) == null)
						entityManager.persist(study);
				}
			// Check if the series is present and add if it is not there
			if (series != null)
				for (Series serie : series) {
					SeriesPK key = new SeriesPK();
					key.setSeriesID(serie.getSeriesID());
					key.setStudyID(serie.getStudyID());
					if (entityManager.find(Series.class, key) == null)
						entityManager.persist(serie);
				}
			// Check if the instance is present and add if it is not there
			if (instances != null)
				for (Instance inst : instances) {
					InstancePK key = new InstancePK();
					key.setInstanceID(inst.getInstanceID());
					key.setSeriesID(inst.getSeriesID());
					if (entityManager.find(Instance.class, key) == null)
						entityManager.persist(inst);
				}
		}
	}

}
