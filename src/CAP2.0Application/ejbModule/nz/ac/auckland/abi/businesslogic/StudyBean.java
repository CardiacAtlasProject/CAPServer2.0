package nz.ac.auckland.abi.businesslogic;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import nz.ac.auckland.abi.entities.Study;
import nz.ac.auckland.abi.entities.StudyMetaData;
import nz.ac.auckland.abi.entities.StudyPACSData;
import nz.ac.auckland.abi.entities.StudyPK;

/**
 * Session Bean implementation class StudyBean
 */
@Stateless
@LocalBean
public class StudyBean {
	@PersistenceContext(unitName = "CAPDS")
	private EntityManager entityManager;
    /**
     * Default constructor. 
     */
    public StudyBean() {
        super();
    }

    public Study addStudy(Study study){
    	StudyPK key = new StudyPK();
    	key.setPatientID(study.getSubject());
    	key.setStudyID(study.getStudyID());
		if (entityManager.find(Study.class,key)==null)
			entityManager.persist(study);
		else{
			entityManager.merge(study);
			entityManager.refresh(study);
		}
		return study;
    }
    
    public Study getStudyByUID(String pid, String uid){
    	StudyPK key = new StudyPK();
    	key.setPatientID(pid);
    	key.setStudyID(uid);
		return entityManager.find(Study.class,key);
    }
    
    public Study getStudyByUID(String uid){
    	@SuppressWarnings("unchecked")
		List<Study> studies = entityManager.createNativeQuery("SELECT * FROM Study u WHERE u.study_id='"+uid+"'", Study.class).getResultList();
    	if(!studies.isEmpty())
    		return studies.get(0);
    	else
    		return null;
    }
    
    @SuppressWarnings("unchecked")
	public List<StudyMetaData> getStudyMetaData(String studyid,String desc){
    	String q = "FROM StudyMetaData p where p.studyID = '"+studyid+"'";
    	if(desc!=null&&desc.length()>0){
    		q = q + " and p.descriptor like '%"+desc+"%'";
    	}
    	Query query = entityManager.createQuery(q);
		return query.getResultList();
    }
    
    
    @SuppressWarnings("unchecked")
	public StudyPACSData getStudyPACSData(String studyid){
    	String q = "FROM StudyPACSData p where p.studyID = '"+studyid+"'";
    	List<StudyPACSData> result = entityManager.createQuery(q).getResultList(); 
		if(result.size()>0)
			return result.get(0);
		return null;
    }
    
    public void setStudyPACSData(String studyid,byte[] data) throws Exception{
    	StudyPACSData PACSdata = getStudyPACSData(studyid);
    	if(PACSdata==null){
    		PACSdata = new StudyPACSData(studyid);
    		PACSdata.setStudyPACSData(data);
    		entityManager.persist(PACSdata);
    	}else{
    		PACSdata.setStudyPACSData(data);
    		entityManager.merge(PACSdata);
    	}
    	entityManager.flush();
    }
}
