package com.yejianfengblue.spring.data.rest.mongofieldnonupdatable.setterchecknull;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

@Repository("setterCheckNullUserRepository")
@RepositoryRestResource(path = "setterCheckNullUsers")
public interface UserRepository extends CrudRepository<User, String> {
}
