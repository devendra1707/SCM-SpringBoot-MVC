package com.smart.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.smart.model.Contact;
import com.smart.model.User;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Integer> {

	// pagination...
	@Query("from Contact as c WHERE c.user.id =:userId")
//	List<Contact> findContactsByUser(@Param("userId") Integer userId);
// Current Page
	// Contact per page
	Page<Contact> findContactsByUser(@Param("userId") Integer userId, Pageable pageable);

	// Search Operation
	List<Contact> findByNameContainingAndUser(String name, User user);

}
