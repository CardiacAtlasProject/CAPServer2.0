package org.cardiacatlas.xpacs.repository;

import org.cardiacatlas.xpacs.domain.PatientInfo;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.*;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the PatientInfo entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PatientInfoRepository extends JpaRepository<PatientInfo,Long> {

	Optional<PatientInfo> findOneByPatientId(String patientId);
	
    @Query("select p.id from PatientInfo p where p.patient_id = ?1")
	Long findID(String patient_id);
}
