package nz.ac.auckland.abi.businesslogic;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import nz.ac.auckland.abi.entities.ModelView;
import nz.ac.auckland.abi.entities.StudyView;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Session Bean implementation class SubjectViewBean
 */
@Stateless
@LocalBean
public class SubjectViewBean implements SubjectViewBeanRemote {

	@PersistenceContext(unitName = "CAPDS")
	private EntityManager entityManager;

	@EJB
	UserProvenanceBean provenance;

	/**
	 * Default constructor.
	 */
	public SubjectViewBean() {
		super();
	}

	public String constructQuery(HashMap<String, String> queryParameters) {
		return null;
	}

	private String createQuery(HashMap<String, String> queryParameters) {
		String[] columnNames = { "subject_id", "subject_name", "subject_birthdate", "subject_gender", "study_description", "study_modalities", "study_date",
				"study_metadata", "", "study_id", "model_name" };
		int amount = 10;
		int start = 0;
		int col = 0;

		String dir = "asc";
		// Datatables related parameters
		String sStart = queryParameters.get("start");
		String sAmount = queryParameters.get("length");
		String sCol = queryParameters.get("orderby");
		String sdir = queryParameters.get("orderdirection");

		String id = queryParameters.get("subject_id");
		String name = queryParameters.get("subject_name");
		String dob = queryParameters.get("subject_birthdate");
		String gender = queryParameters.get("subject_gender");
		String studydateLower = queryParameters.get("lsd");
		String studydateUpper = queryParameters.get("usd");

		List<String> sArray = new ArrayList<String>();
		if (id != null) {
			String sBrowser = " subject_id ";
			if (id.indexOf('*') < 0 && id.indexOf('+') < 0) {
				sBrowser += " like '%" + id + "%'";
			} else {
				sBrowser += " REGEXP '" + id + "'";
			}
			sArray.add(sBrowser);
			// or combine the above two steps as:

			// sArray.add(" engine like '%" + engine + "%'");
			// the same as followings
		}
		if (name != null) {
			String sBrowser = " subject_name ";
			if (id.indexOf('*') < 0 && name.indexOf('+') < 0) {
				sBrowser += " like '%" + name + "%'";
			} else {
				sBrowser += " REGEXP '" + name + "'";
			}
			sArray.add(sBrowser);
		}
		if (dob != null) {
			String sBrowser = " subject_birthdate ";
			if (id.indexOf('*') < 0 && dob.indexOf('+') < 0) {
				sBrowser += " like '%" + dob + "%'";
			} else {
				sBrowser += " REGEXP '" + dob + "'";
			}
			sArray.add(sBrowser);
		}
		if (gender != null) {
			String sBrowser = " subject_gender ";
			if (id.indexOf('*') < 0 && gender.indexOf('+') < 0) {
				sBrowser += " like '%" + gender + "%'";
			} else {
				sBrowser += " REGEXP '" + gender + "'";
			}
			sArray.add(sBrowser);
		}
		if (studydateLower != null) {
			String sBrowser = " study_date < '" + studydateLower + "'";
			sArray.add(sBrowser);
		}
		if (studydateUpper != null) {
			String sBrowser = " study_date > '" + studydateUpper + "'";
			sArray.add(sBrowser);
		}
		String individualSearch = "";
		if (sArray.size() == 1) {
			individualSearch = sArray.get(0);
		} else if (sArray.size() > 1) {
			for (int i = 0; i < sArray.size() - 1; i++) {
				individualSearch += sArray.get(i) + " and ";
			}
			individualSearch += sArray.get(sArray.size() - 1);
		}

		if (sStart != null) {
			start = Integer.parseInt(sStart);
			if (start < 0)
				start = 0;
		}
		if (sAmount != null) {
			amount = Integer.parseInt(sAmount);
			if (amount < 10 || amount > 100)
				amount = 10;
		}
		if (sCol != null) {
			col = Integer.parseInt(sCol);
			if (col > 4 && col != 6 && col != 10)
				col = 0;
		}
		if (sdir != null) {
			if (!sdir.equals("asc"))
				dir = "desc";
		}
		String colName = columnNames[col];

		// Now load the array with records that math the search key words

		String searchSQL = "";
		String searchTerm = queryParameters.get("sSearch");

		if (searchTerm != null) {
			String globeSearch = " where (p.subject_id like '%" + searchTerm + "%'" + " or p.subject_name like '%" + searchTerm + "%'"
					+ " or p.subject_birthdate like '%" + searchTerm + "%'" + " or p.subject_gender like '%" + searchTerm + "%')";
			if (searchTerm != "" && individualSearch != "") {
				searchSQL = globeSearch + " and " + individualSearch;

			} else if (individualSearch != "") {
				searchSQL = " where " + individualSearch;

			} else if (searchTerm != "") {
				searchSQL = globeSearch;

			}
		} else if (individualSearch != "") {
			searchSQL = " where " + individualSearch;

		}
		if (colName != null)
			searchSQL += " order by p." + colName + " " + dir;
		searchSQL += " limit " + start + ", " + amount;
		return searchSQL;
	}

	@SuppressWarnings("unchecked")
	public JSONObject queryImageData(String user, HashMap<String, String> queryParameters) {
		String cqp = createQuery(queryParameters);
		provenance.logUsage(user, "QUERYIMAGEDATA", cqp, 1);
		JSONObject result = new JSONObject();
		JSONArray array = new JSONArray();

		// Datatables related parameters
		String sStart = queryParameters.get("start");
		String sAmount = queryParameters.get("length");
		String draw = queryParameters.get("draw");

		result.put("start", sStart);
		result.put("count", sAmount);

		try {
			// Get the total number of studyrecords
			String q = "SELECT count(*) from  StudyView";
			Query countq = entityManager.createNativeQuery(q);
			List<java.math.BigInteger> pctr = countq.getResultList();

			// Determine the list of patients, let this be total
			int total = pctr.get(0).intValue();

			// Now load the array with records that math the search key words

			// Get the list of subjects that match the query
			q = "SELECT * from  StudyView p" + cqp;
			// System.out.println(q);
			Query query = entityManager.createNativeQuery(q, StudyView.class);
			List<StudyView> selectedStudies = query.getResultList();
			int totalAfterFilter = selectedStudies.size();

			// Build JSON
			for (StudyView s : selectedStudies) {
				JSONObject viewrecord = new JSONObject();
				viewrecord.put("DT_RowId", s.getStudyID());// DT_RowId allows
				// datatables to set the
				// column as id
				viewrecord.put("ID", s.getId());
				viewrecord.put("NAME", s.getName());
				viewrecord.put("DOB", s.getBirthdate());
				viewrecord.put("GENDER", s.getGender());

				viewrecord.put("UID", s.getStudyID());
				viewrecord.put("DESC", s.getStudyDescription());
				viewrecord.put("MODALITIES", s.getStudyModalities());
				byte[] metaData = s.getStudyMetaData();
				if (metaData != null)
					viewrecord.put("METADATA", new String(metaData, Charset.forName("UTF-8")));
				else
					viewrecord.put("METADATA", "");
				viewrecord.put("DATE", "" + s.getStudyDate());
				viewrecord.put("SERIES", s.getJson());

				array.add(viewrecord);
			}
			// Based on https://datatables.net/manual/server-side //access date
			// Jan 2015
			result.put("recordsTotal", total);
			result.put("recordsFiltered", totalAfterFilter);
			result.put("data", array);
			if (draw != null)
				result.put("draw", draw);
		} catch (Exception exx) {
			result.put("error", exx.toString());
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public JSONObject queryModelData(String user, HashMap<String, String> queryParameters) {
		String cqp = createQuery(queryParameters);
		provenance.logUsage(user, "QUERYMODELDATA", cqp, 1);
		JSONObject result = new JSONObject();
		JSONArray array = new JSONArray();

		// Datatables related parameters
		String sStart = queryParameters.get("start");
		String sAmount = queryParameters.get("length");
		String draw = queryParameters.get("draw");

		result.put("start", sStart);
		result.put("count", sAmount);

		try {
			// Get the total number of studyrecords
			String q = "SELECT count(*) from  ModelView";
			Query countq = entityManager.createNativeQuery(q);
			List<java.math.BigInteger> pctr = countq.getResultList();

			// Determine the list of patients, let this be total
			int total = pctr.get(0).intValue();

			// Now load the array with records that math the search key words

			// Get the list of subjects that match the query
			q = "SELECT * from  ModelView p" + cqp;
			// System.out.println(q);
			Query query = entityManager.createNativeQuery(q, ModelView.class);
			List<ModelView> selectedModels = query.getResultList();
			int totalAfterFilter = selectedModels.size();

			// Build JSON
			for (ModelView s : selectedModels) {
				JSONObject viewrecord = new JSONObject();
				viewrecord.put("DT_RowId", s.getModelID());// DT_RowId allows
				// datatables to set the
				// column as id
				viewrecord.put("ID", s.getSubjectID());
				viewrecord.put("NAME", s.getSubjectName());
				viewrecord.put("DOB", s.getBirthdate());
				viewrecord.put("GENDER", s.getGender());

				viewrecord.put("UID", s.getStudyID());
				viewrecord.put("DATE", "" + s.getStudyDate());
				viewrecord.put("DESC", s.getStudyDescription());
				viewrecord.put("MODALITIES", s.getStudyModalities());
				viewrecord.put("MODELID", s.getModelID());
				viewrecord.put("MODELNAME", s.getModelName());
				viewrecord.put("COMMENTS", s.getModelComments());
				viewrecord.put("HASMETADATA", "" + s.getHasMetaData());

				array.add(viewrecord);
			}
			// Based on https://datatables.net/manual/server-side //access date
			// Jan 2015
			result.put("recordsTotal", total);
			result.put("recordsFiltered", totalAfterFilter);
			result.put("data", array);
			if (draw != null)
				result.put("draw", draw);
		} catch (Exception exx) {
			result.put("error", exx.toString());
		}
		return result;
	}

}
