package com.project.service;

import java.util.List;
<<<<<<< HEAD

=======
import java.util.Map;

import com.project.dto.BugRequestDto;
import com.project.dto.BugResponseDTO;
import com.project.dto.PriorityCountDTO;
import com.project.dto.StatusCountDTO;
import com.project.dto.StatusCountPresentationDTO;
>>>>>>> b5a9c0e (security changed for cors)
import com.project.model.Bug;
import com.project.model.BugStatus;

public interface BugService {

<<<<<<< HEAD
	Bug createBug(Bug bug, String requestedRole) throws Exception;
	
	Bug getBugById(Long id) throws Exception;
	
	List<Bug> getAllBugs(BugStatus status);
	
	Bug updateBug(Long id, Bug updatedBug, Long userId) throws Exception;
=======
	Bug createBug(BugRequestDto bugRequest, String requestedRole) throws Exception;
	
	BugResponseDTO getBugById(Long id) throws Exception;
	
	List<BugResponseDTO> getAllBugs(BugStatus status);
	
	BugResponseDTO updateBug(Long id, Bug updatedBug, Long userId) throws Exception;
>>>>>>> b5a9c0e (security changed for cors)
	
	void deleteBug(Long id) throws Exception;
	
	Bug assignedToUser(Long userId, Long taskId) throws Exception;
	
<<<<<<< HEAD
	List<Bug> assignedUsersBug(Long userId, BugStatus status);
	
	Bug completedBug(Long bugId) throws Exception;
	
=======
	List<BugResponseDTO> assignedUsersBug(Long userId, BugStatus status);
	List<BugResponseDTO> getAllBugsByProjectId(Long projectId );
	
	Bug completedBug(Long bugId) throws Exception;
	Map<String, Long> getBugCountByStatus();
	Map<String, Long> getBugCountByPriority();
	List<PriorityCountDTO> getBugCountByPriorityForUser(Long userId);
//	public List<StatusCountDTO> getBugCountsByStatus() ;
//	
//	public List<StatusCountPresentationDTO> getUserBugCountsByStatus(Long userId);
	public Bug updateBugStatus(Long bugId, BugStatus newStatus);
;	
>>>>>>> b5a9c0e (security changed for cors)
}
