package nz.ac.auckland.abi.businesslogic;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import nz.ac.auckland.abi.entities.Instance;
import nz.ac.auckland.abi.entities.InstancePK;

/**
 * Session Bean implementation class SubjectBean
 */
@Stateless
@LocalBean
public class InstanceBean {

	@PersistenceContext(unitName = "CAPDS")
	private EntityManager entityManager;
    /**
     * Default constructor. 
     */
    public InstanceBean() {
        super();
    }
    
    public Instance addInstance(Instance inst){
    	InstancePK key = new InstancePK();
    	key.setInstanceID(inst.getInstanceID());
    	key.setSeriesID(inst.getSeriesID());
		if (entityManager.find(Instance.class,key)==null)
			entityManager.persist(inst);
		else{
			entityManager.merge(inst);
			entityManager.refresh(inst);
		}
		return inst;
    }
    
    public void removeInstance(Instance inst){
    	InstancePK key = new InstancePK();
    	key.setInstanceID(inst.getInstanceID());
    	key.setSeriesID(inst.getSeriesID());
		Instance del = entityManager.find(Instance.class, key);
		if (del != null) {
			entityManager.refresh(del);
			entityManager.flush();
		}
    }
}
