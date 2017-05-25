package org.cardiacatlas.xpacs.repository;

import org.cardiacatlas.xpacs.domain.CapModel;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the CapModel entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CapModelRepository extends JpaRepository<CapModel,Long> {

}
