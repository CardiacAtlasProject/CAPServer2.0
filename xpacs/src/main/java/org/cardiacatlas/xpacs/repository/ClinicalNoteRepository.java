package org.cardiacatlas.xpacs.repository;

import org.cardiacatlas.xpacs.domain.ClinicalNote;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the ClinicalNote entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ClinicalNoteRepository extends JpaRepository<ClinicalNote,Long> {

}
