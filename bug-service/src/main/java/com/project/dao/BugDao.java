package com.project.dao;

<<<<<<< HEAD
import com.project.model.Bug;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BugDao extends JpaRepository<Bug, Long>{
	
	public List<Bug> findByAssignedUserId(Long userId);

=======
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.project.dto.StatusCountDTO;
import com.project.model.Bug;

public interface BugDao extends JpaRepository<Bug, Long> {

	public List<Bug> findByAssignedUserId(Long userId);

	@Query("SELECT b.priority, COUNT(b) FROM Bug b GROUP BY b.priority")
	public List<Object[]> countByPriority();

	@Query("SELECT b.status, COUNT(b) FROM Bug b GROUP BY b.status")
	public List<Object[]> countByStatus();

	List<Bug> findByProjectId(Long projectId);
	
	@Query("SELECT b.priority AS priority, COUNT(b) AS count FROM Bug b WHERE b.assignedUserId = :userId GROUP BY b.priority")
    List<Object[]> countByPriorityForUser(Long userId);
    
//    
//    @Query("SELECT new com.project.dto.StatusCountDTO(b.status, COUNT(b)) " +
//            "FROM Bug b GROUP BY b.status")
//     List<StatusCountDTO> countBugsGroupedByStatus();
//    
//    @Query("SELECT new com.project.dto.StatusCountDTO(b.status, COUNT(b)) " +
//    	       "FROM Bug b WHERE b.assignedUserId = :userId GROUP BY b.status")
//    	List<StatusCountDTO> countBugsGroupedByStatusByUser( Long userId);
    
>>>>>>> b5a9c0e (security changed for cors)
}
