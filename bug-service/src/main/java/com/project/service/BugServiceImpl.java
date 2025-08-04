package com.project.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.dao.BugDao;
import com.project.model.Bug;
import com.project.model.BugStatus;

@Service
public class BugServiceImpl implements BugService {

	@Autowired
	private BugDao bugDao; 
	
	@Override
	public Bug createBug(Bug bug, String requestedRole) throws Exception {
		if(!requestedRole.equals(("ROLE_ADMIN"))) {
			throw new Exception("Only admin can upload a bug!");
		}
		bug.setStatus(BugStatus.PENDING);
		bug.setCreatedAt(LocalDateTime.now());
		
		return bugDao.save(bug);
	}

	@Override
	public Bug getBugById(Long id) throws Exception {
		return bugDao.findById(id).orElseThrow(() -> new Exception("Bug not found with id: " + id));
	}

	@Override
	public List<Bug> getAllBugs(BugStatus status) {
		List<Bug> allBugs = bugDao.findAll();
		
		List<Bug> filteredBugs = allBugs.stream().filter(
				bug -> status == null || bug.getStatus().name().equalsIgnoreCase(status.toString())
				).collect(Collectors.toList());
		return filteredBugs;
	}

	@Override
	public Bug updateBug(Long id, Bug updatedBug, Long userId) throws Exception {
		Bug existingBug = getBugById(id);
		
		if(updatedBug.getTitle()!=null) {
			existingBug.setTitle(updatedBug.getTitle());
		}
		
		if(updatedBug.getImage()!=null) {
			existingBug.setImage(updatedBug.getImage());
		}
		
		if(updatedBug.getDescription()!=null) {
			existingBug.setDescription(updatedBug.getDescription());
		}
		
		if(updatedBug.getStatus()!=null) {
			existingBug.setStatus(updatedBug.getStatus());
		}
		
		if(updatedBug.getDeadLine()!=null) {
			existingBug.setDeadLine(updatedBug.getDeadLine());
		}
		return bugDao.save(existingBug);
	}

	@Override
	public void deleteBug(Long id) throws Exception {
		
		getBugById(id);
		
		bugDao.deleteById(id);
		
	}

	@Override
	public Bug assignedToUser(Long userId, Long bugId) throws Exception {
		
		Bug bug = getBugById(bugId);
		bug.setAssignedUserId(userId);
		bug.setStatus(BugStatus.DONE);
		
		return bugDao.save(bug);
	}

	@Override
	public List<Bug> assignedUsersBug(Long userId, BugStatus status) {
		
		List<Bug> allBugs = bugDao.findByAssignedUserId(userId);
		
		List<Bug> filteredBugs = allBugs.stream().filter(
				bug -> status == null || bug.getStatus().name().equalsIgnoreCase(status.toString())
				).collect(Collectors.toList());
		return filteredBugs;
		
	}

	@Override
	public Bug completedBug(Long bugId) throws Exception {
		Bug bug = getBugById(bugId);
		bug.setStatus(BugStatus.DONE);
		return bugDao.save(bug);
	}

}
