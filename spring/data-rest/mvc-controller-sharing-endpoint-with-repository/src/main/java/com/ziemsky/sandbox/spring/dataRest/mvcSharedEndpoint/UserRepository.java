package com.ziemsky.sandbox.spring.dataRest.mvcSharedEndpoint;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}
