package com.project.dao;

import com.project.model.Bug;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BugDao extends JpaRepository<Bug, Long>{
	
	public List<Bug> findByAssignedUserId(Long userId);

}
