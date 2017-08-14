package org.cardiacatlas.xpacs.repository;

import org.cardiacatlas.xpacs.domain.ClinicalNote;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the ClinicalNote entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ClinicalNoteRepository extends JpaRepository<ClinicalNote,Long> {

	@Query("select c from ClinicalNote c INNER JOIN PatientInfo p on c.patientInfoFK = p.id where p.id = ?1")
	List<ClinicalNote> findAllByID(Long id);
}
