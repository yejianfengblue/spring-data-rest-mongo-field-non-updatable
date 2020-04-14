package com.yejianfengblue.spring.data.rest.mongofieldnonupdatable.transient_;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository("transientUserRepository")
interface UserRepository extends CrudRepository<User, String> {
}
