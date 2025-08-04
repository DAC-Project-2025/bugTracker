package com.project.service;

import java.util.List;

import com.project.model.Bug;
import com.project.model.BugStatus;

public interface BugService {

	Bug createBug(Bug bug, String requestedRole) throws Exception;
	
	Bug getBugById(Long id) throws Exception;
	
	List<Bug> getAllBugs(BugStatus status);
	
	Bug updateBug(Long id, Bug updatedBug, Long userId) throws Exception;
	
	void deleteBug(Long id) throws Exception;
	
	Bug assignedToUser(Long userId, Long taskId) throws Exception;
	
	List<Bug> assignedUsersBug(Long userId, BugStatus status);
	
	Bug completedBug(Long bugId) throws Exception;
	
}
