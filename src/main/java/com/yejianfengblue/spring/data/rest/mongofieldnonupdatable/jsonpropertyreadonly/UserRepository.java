package com.yejianfengblue.spring.data.rest.mongofieldnonupdatable.jsonpropertyreadonly;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

@Repository("jsonPropertyReadOnlyUserRepository")
@RepositoryRestResource(path = "jsonPropertyReadOnlyUsers")
public interface UserRepository extends CrudRepository<User, String> {
}
