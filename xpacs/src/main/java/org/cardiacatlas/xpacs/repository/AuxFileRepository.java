package org.cardiacatlas.xpacs.repository;

import org.cardiacatlas.xpacs.domain.AuxFile;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the AuxFile entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AuxFileRepository extends JpaRepository<AuxFile,Long> {

	@Query("select c from AuxFile c INNER JOIN PatientInfo p on c.patientInfoFK = p.id where p.id = ?1")
	List<AuxFile> findAllByID(Long id);
}
