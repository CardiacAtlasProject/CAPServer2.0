package org.cardiacatlas.xpacs.repository;

import org.cardiacatlas.xpacs.domain.DICOM;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the DICOM entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DICOMRepository extends JpaRepository<DICOM,Long> {
    
}
