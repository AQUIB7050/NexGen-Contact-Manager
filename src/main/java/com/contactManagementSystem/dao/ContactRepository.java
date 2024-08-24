package com.contactManagementSystem.dao;



import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.contactManagementSystem.entities.Contact;
import com.contactManagementSystem.entities.User;

public interface ContactRepository extends JpaRepository<Contact, Integer> {
	
	
	//Page basically is subset of Collection
	//Pageable have two information :- currentpage and contact per page
	@Query("from Contact as c where c.user.id =:userId")
	public Page<Contact> findContactByUser(@Param("userId")int userId, Pageable pageable);
	
	//For SEARCH
	public List<Contact> findByNameContainingAndUser(String name, User user);
	
	public List<Contact> findByNameContaining(String name);

}
