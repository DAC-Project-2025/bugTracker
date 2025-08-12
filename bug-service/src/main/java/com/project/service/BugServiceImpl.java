package com.project.service;

<<<<<<< HEAD
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

=======
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
>>>>>>> b5a9c0e (security changed for cors)
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.dao.BugDao;
<<<<<<< HEAD
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

=======
import com.project.dto.BugRequestDto;
import com.project.dto.BugResponseDTO;
import com.project.dto.PriorityCountDTO;
import com.project.dto.StatusCountDTO;
import com.project.dto.StatusCountPresentationDTO;
import com.project.model.Bug;
import com.project.model.BugStatus;
import com.project.model.Priority;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class BugServiceImpl implements BugService {

    @Autowired
    private BugDao bugDao;

    private final ModelMapper modelMapper;

    /** Helper: Fetch entity by ID or throw exception */
    private Bug getBugEntityById(Long id) throws Exception {
        return bugDao.findById(id)
                .orElseThrow(() -> new Exception("Bug not found with id: " + id));
    }

    @Override
    public Bug createBug(BugRequestDto bugRequest, String requestedRole) throws Exception {
        if (!requestedRole.equals("ADMIN") && !requestedRole.equals("MANAGER")) {
            throw new Exception("Only admin or manager can upload a bug!");
        }

        Bug bug = new Bug();
        bug.setProjectId(bugRequest.getProjectId());
        bug.setTitle(bugRequest.getTitle());
        bug.setDescription(bugRequest.getDescription());
        bug.setAssignedUserId(bugRequest.getAssignedUserId());
        bug.setDeadLine(bugRequest.getDeadLine());
        bug.setStatus(BugStatus.ASSIGNED);
        bug.setCreatedAt(LocalDate.now());
        bug.setEnvironment(bugRequest.getEnvironment());
        bug.setBrowser(bugRequest.getBrowser());
        bug.setFiles(bugRequest.getFiles());
        bug.setPriority(bugRequest.getPriority());
        bug.setOs(bugRequest.getOs());
        return bugDao.save(bug);
    }

    @Override
    public BugResponseDTO getBugById(Long id) throws Exception {
        Bug bug = getBugEntityById(id);
        return modelMapper.map(bug, BugResponseDTO.class);
    }

    @Override
    public List<BugResponseDTO> getAllBugs(BugStatus status) {
        return bugDao.findAll().stream()
                .filter(bug -> status == null || bug.getStatus() == status)
                .map(bug -> modelMapper.map(bug, BugResponseDTO.class))
                .toList();
    }

    @Override
    public BugResponseDTO updateBug(Long id, Bug updatedBug, Long userId) throws Exception {
        Bug existingBug = getBugEntityById(id);

        if (updatedBug.getTitle() != null) existingBug.setTitle(updatedBug.getTitle());
        if (updatedBug.getDescription() != null) existingBug.setDescription(updatedBug.getDescription());
        if (updatedBug.getStatus() != null) existingBug.setStatus(updatedBug.getStatus());
        if (updatedBug.getDeadLine() != null) existingBug.setDeadLine(updatedBug.getDeadLine());

        Bug savedBug = bugDao.save(existingBug);
        return modelMapper.map(savedBug, BugResponseDTO.class);
    }

    @Override
    public void deleteBug(Long id) throws Exception {
        getBugEntityById(id);
        bugDao.deleteById(id);
    }

    @Override
    public Bug assignedToUser(Long userId, Long bugId) throws Exception {
        Bug bug = getBugEntityById(bugId);
        bug.setAssignedUserId(userId);
        bug.setStatus(BugStatus.DONE);
        return bugDao.save(bug);
    }

    @Override
    public List<BugResponseDTO> assignedUsersBug(Long userId, BugStatus status) {
        return bugDao.findByAssignedUserId(userId).stream()
                .filter(bug -> status == null || bug.getStatus() == status)
                .map(bug -> modelMapper.map(bug, BugResponseDTO.class))
                .toList();
    }

    @Override
    public Bug completedBug(Long bugId) throws Exception {
        Bug bug = getBugEntityById(bugId);
        bug.setStatus(BugStatus.DONE);
        return bugDao.save(bug);
    }

    public Map<String, Long> getBugCountByPriority() {
        List<Object[]> results = bugDao.countByPriority();
        Map<String, Long> counts = new HashMap<>();
        for (Object[] row : results) {
            String key = row[0] != null ? row[0].toString() : "UNKNOWN";
            Long value = row[1] != null ? (Long) row[1] : 0L;
            counts.put(key, value);
        }
        counts.putIfAbsent("HIGH", 0L);
        counts.putIfAbsent("MEDIUM", 0L);
        counts.putIfAbsent("LOW", 0L);
        counts.putIfAbsent("UNKNOWN", 0L);
        return counts;
    }

    public Map<String, Long> getBugCountByStatus() {
        List<Object[]> results = bugDao.countByStatus();
        Map<String, Long> counts = new HashMap<>();
        for (Object[] row : results) {
            String key = row[0] != null ? row[0].toString() : "UNKNOWN";
            Long value = row[1] != null ? (Long) row[1] : 0L;
            counts.put(key, value);
        }
        counts.putIfAbsent("INPROGRESS", 0L);
        counts.putIfAbsent("ASSIGNED", 0L);
        counts.putIfAbsent("DONE", 0L);
        counts.putIfAbsent("UNKNOWN", 0L);
        return counts;
    }

    @Override
    public List<BugResponseDTO> getAllBugsByProjectId(Long projectId) {
        return bugDao.findByProjectId(projectId).stream()
                .map(bug -> modelMapper.map(bug, BugResponseDTO.class))
                .toList();
    }

	@Override
	public List<PriorityCountDTO> getBugCountByPriorityForUser(Long userId) {
		List<Object[]> results = bugDao.countByPriorityForUser(userId);

	    Map<Priority, Long> countsMap = new HashMap<>();
	    for (Object[] row : results) {
	        Priority priority = (Priority) row[0];
	        Long count = (Long) row[1];
	        countsMap.put(priority, count);
	    }

	    List<PriorityCountDTO> list = new ArrayList<>();
	    list.add(new PriorityCountDTO(Priority.HIGH, countsMap.getOrDefault(Priority.HIGH, 0L)));
	    list.add(new PriorityCountDTO(Priority.MEDIUM, countsMap.getOrDefault(Priority.MEDIUM, 0L)));
	    list.add(new PriorityCountDTO(Priority.LOW, countsMap.getOrDefault(Priority.LOW, 0L)));

	    return list;
	}

//	@Override
//	public List<StatusCountDTO> getBugCountsByStatus() {
//		List<StatusCountDTO> rawCounts = bugDao.countBugsGroupedByStatus();
//
//	    return rawCounts.stream()
//	            .map(dto -> {
//	                String friendlyStatus;
//	                switch(dto.getStatus()) {  // dto.getStatus() is BugStatus enum
//	                    case DONE:
//	                        friendlyStatus = "Completed";
//	                        break;
//	                    case INPROGRESS:
//	                        friendlyStatus = "In-Progress";
//	                        break;
//	                    case ASSIGNED:
//	                        friendlyStatus = "Pending";
//	                        break;
//	                    case REASSIGNED:
//	                        friendlyStatus = "Pending";
//	                        break;
//	                    case REVIEW:
//	                        friendlyStatus = "In-Review";
//	                        break;
//	                    default:
//	                        friendlyStatus = dto.getStatus().name();
//	                }
//	                return new StatusCountDTO(dto.getStatus(), dto.getCount());
//	                // Or, if you want to return friendly string:
//	                // return new StatusCountDTO(friendlyStatus, dto.getCount());
//	                // But then StatusCountDTO.status must be String
//	            })
//	            .collect(Collectors.toList());
//	}
//
//
//	@Override
//	public List<StatusCountPresentationDTO> getUserBugCountsByStatus(Long userId) {
//	    List<StatusCountDTO> rawCounts = bugDao.countBugsGroupedByStatusByUser(userId);
//
//	    return rawCounts.stream()
//	            .map(dto -> {
//	                String friendlyStatus;
//	                switch(dto.getStatus()) {
//	                    case DONE:
//	                        friendlyStatus = "Completed";
//	                        break;
//	                    case INPROGRESS:
//	                        friendlyStatus = "In-Progress";
//	                        break;
//	                    case ASSIGNED:
//	                    case REASSIGNED:
//	                        friendlyStatus = "Pending";
//	                        break;
//	                    case REVIEW:
//	                        friendlyStatus = "In-Review";
//	                        break;
//	                    default:
//	                        friendlyStatus = dto.getStatus().name();
//	                }
//	                return new StatusCountPresentationDTO(friendlyStatus, dto.getCount());
//	            })
//	            .collect(Collectors.toList());
//	}
	
	@Override
	 public Bug updateBugStatus(Long bugId, BugStatus newStatus) {
	        Bug bug = bugDao.findById(bugId)
	            .orElseThrow(() -> new RuntimeException("Bug not found with id " + bugId));

	        bug.setStatus(newStatus);
	        return bugDao.save(bug);
	    }
	
	
	
	
    
>>>>>>> b5a9c0e (security changed for cors)
}
