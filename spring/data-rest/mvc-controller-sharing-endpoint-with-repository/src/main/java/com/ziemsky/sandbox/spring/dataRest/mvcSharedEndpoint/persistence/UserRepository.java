package com.ziemsky.sandbox.spring.dataRest.mvcSharedEndpoint.persistence;

import com.ziemsky.sandbox.spring.dataRest.mvcSharedEndpoint.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}
