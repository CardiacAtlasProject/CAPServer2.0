package org.cardiacatlas.xpacs.repository;

import org.cardiacatlas.xpacs.domain.AuxFile;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the AuxFile entity.
 */
@SuppressWarnings("unused")
public interface AuxFileRepository extends JpaRepository<AuxFile,Long> {

}
