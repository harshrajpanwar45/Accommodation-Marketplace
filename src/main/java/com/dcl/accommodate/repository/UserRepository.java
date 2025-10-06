package com.dcl.accommodate.repository;

import com.dcl.accommodate.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
}
