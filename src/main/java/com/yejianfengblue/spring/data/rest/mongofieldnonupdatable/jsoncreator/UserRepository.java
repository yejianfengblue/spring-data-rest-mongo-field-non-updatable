package com.yejianfengblue.spring.data.rest.mongofieldnonupdatable.jsoncreator;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

@Repository("jsonCreatorUserRepository")
@RepositoryRestResource(path = "jsonCreatorUsers")
public interface UserRepository extends CrudRepository<User, String> {
}
