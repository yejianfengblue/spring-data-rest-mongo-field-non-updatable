package com.yejianfengblue.spring.data.rest.mongofieldnonupdatable.nosetter;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

@Repository("noSetterUserRepository")
@RepositoryRestResource(path = "noSetterUsers")
interface UserRepository extends CrudRepository<User, String> {
}
