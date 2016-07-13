package nz.ac.auckland.abi.businesslogic;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import nz.ac.auckland.abi.entities.Model;

/**
 * Session Bean implementation class SubjectBean
 */
@Stateless
@LocalBean
public class ModelBean {

	@PersistenceContext(unitName = "CAPDS")
	private EntityManager entityManager;

	/**
	 * Default constructor.
	 */
	public ModelBean() {
		super();
	}

	public Model addModel(Model model){
		entityManager.persist(model);
		entityManager.flush();
		return model;
	}

	public void removeModel(Model model) {
		entityManager.createNativeQuery("DELETE FROM Model where model_id='"+model.getModelID()+"'").executeUpdate();
		entityManager.flush();
	}

	public Model getModel(String id) {
		Long key = new Long(id);
		return entityManager.find(Model.class, key);
	}

	public List<Model> getModels(String studyUID) {
		return entityManager.createQuery("SELECT T FROM Model T WHERE T.studyID='" + studyUID + "'", Model.class).getResultList();
	}

}
