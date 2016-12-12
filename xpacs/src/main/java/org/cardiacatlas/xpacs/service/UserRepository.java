package org.cardiacatlas.xpacs.service;

import org.cardiacatlas.xpacs.domain.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {

}
