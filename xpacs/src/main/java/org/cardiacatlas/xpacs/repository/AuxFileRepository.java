package org.cardiacatlas.xpacs.repository;

import org.cardiacatlas.xpacs.domain.AuxFile;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the AuxFile entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AuxFileRepository extends JpaRepository<AuxFile,Long> {

}
