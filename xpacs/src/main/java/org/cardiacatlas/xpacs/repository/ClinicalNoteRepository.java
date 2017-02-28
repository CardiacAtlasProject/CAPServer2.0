package org.cardiacatlas.xpacs.repository;

import org.cardiacatlas.xpacs.domain.ClinicalNote;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the ClinicalNote entity.
 */
@SuppressWarnings("unused")
public interface ClinicalNoteRepository extends JpaRepository<ClinicalNote,Long> {

}
