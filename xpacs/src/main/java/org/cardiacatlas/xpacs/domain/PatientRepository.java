package org.cardiacatlas.xpacs.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository< Patient, Integer > {

}
