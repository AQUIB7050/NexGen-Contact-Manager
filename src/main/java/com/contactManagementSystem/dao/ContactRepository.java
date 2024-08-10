package com.contactManagementSystem.dao;



import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.contactManagementSystem.entities.Contact;

public interface ContactRepository extends JpaRepository<Contact, Integer> {
	
	
	//Page basically is subset of Collection
	//Pageable have two information :- currentpage and contact per page
	@Query("from Contact as c where c.user.id =:userId")
	public Page<Contact> findContactByUser(@Param("userId")int userId, Pageable pageable);

}
