package org.cardiacatlas.xpacs.repository;

import org.cardiacatlas.xpacs.domain.CapModel;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the CapModel entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CapModelRepository extends JpaRepository<CapModel,Long> {

	@Query("select c from CapModel c INNER JOIN PatientInfo p on c.patientInfoFK = p.id where p.id = ?1")
	List<CapModel> findAllByID(Long id);
}
