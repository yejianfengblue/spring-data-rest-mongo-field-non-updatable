package com.yejianfengblue.spring.data.rest.mongofieldnonupdatable.readonlyproperty;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository("readOnlyPropertyUserRepository")
interface UserRepository extends CrudRepository<User, String> {
}
