package com.yejianfengblue.spring.data.rest.mongofieldnonupdatable.expected;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

@Repository("expectedUserRepository")
@RepositoryRestResource(path = "expectedUsers")
public interface UserRepository extends CrudRepository<User, String> {
}
