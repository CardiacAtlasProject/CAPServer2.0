package nz.ac.auckland.abi.businesslogic;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import nz.ac.auckland.abi.entities.Series;
import nz.ac.auckland.abi.entities.SeriesPK;

/**
 * Session Bean implementation class SubjectBean
 */
@Stateless
@LocalBean
public class SeriesBean {

	@PersistenceContext(unitName = "CAPDS")
	private EntityManager entityManager;
    /**
     * Default constructor. 
     */
    public SeriesBean() {
        super();
    }
    
    public Series addSeries(Series series){
    	SeriesPK key = new SeriesPK();
    	key.setSeriesID(series.getSeriesID());
    	key.setStudyID(series.getStudyID());
		if (entityManager.find(Series.class,key)==null)
			entityManager.persist(series);
		else{
			entityManager.merge(series);//So that it is managed
			entityManager.refresh(series);
		}
		return series;
    }
    
    public void removeSeries(Series series){
       	SeriesPK key = new SeriesPK();
    	key.setSeriesID(series.getSeriesID());
    	key.setStudyID(series.getStudyID());
		Series del = entityManager.find(Series.class, key);
		if (del != null) {
			entityManager.refresh(del);
			entityManager.flush();
		}
    }
}
