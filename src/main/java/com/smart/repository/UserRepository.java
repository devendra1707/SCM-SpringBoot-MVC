package com.smart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {

	@Query("select u from User u where u.email= :email")
	User getUserByUserName(@Param("email") String email);
}
