package com.project.controller;

<<<<<<< HEAD
import java.util.List;
=======
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
>>>>>>> b5a9c0e (security changed for cors)

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
<<<<<<< HEAD
=======
import org.springframework.security.access.prepost.PreAuthorize;
>>>>>>> b5a9c0e (security changed for cors)
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

<<<<<<< HEAD
=======
import com.project.dao.BugDao;
import com.project.dto.BugRequestDto;
import com.project.dto.BugResponseDTO;
import com.project.dto.PriorityCountDTO;
import com.project.dto.StatusCountDTO;
>>>>>>> b5a9c0e (security changed for cors)
import com.project.dto.UserDto;
import com.project.model.Bug;
import com.project.model.BugStatus;
import com.project.service.BugService;
import com.project.service.UserService;

@RestController
@RequestMapping("/api/bugs")
public class BugController {

	@Autowired
	private BugService bugService;
<<<<<<< HEAD
	
	@Autowired
	private UserService userService;
	
	@PostMapping
	public ResponseEntity<Bug> createBug(@RequestBody Bug bug, @RequestHeader("Authorization") String jwt) throws Exception{
		
		UserDto user = userService.getUserProfile(jwt);
		
		Bug createdBug = bugService.createBug(bug, user.getRole());
		
		return new ResponseEntity<>(createdBug, HttpStatus.CREATED);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Bug> getBugById(@PathVariable Long id, @RequestHeader("Authorization") String jwt ) throws Exception{
		
		UserDto user = userService.getUserProfile(jwt);
		
		Bug bug = bugService.getBugById(id);
		
		return new ResponseEntity<>(bug, HttpStatus.OK);
	}
	
	@GetMapping("/user")
	public ResponseEntity<List<Bug>> getAssignedUsersBug(@RequestParam(required = false) BugStatus status, @RequestHeader("Authorization") String jwt) throws Exception{
		
		UserDto user = userService.getUserProfile(jwt);
		
		List<Bug> bugs = bugService.assignedUsersBug(user.getId(), status);
		
		return new ResponseEntity<>(bugs, HttpStatus.OK);
	}
	
	@GetMapping()
	public ResponseEntity<List<Bug>> getAllBug(@RequestParam(required = false) BugStatus status, @RequestHeader("Authorization") String jwt) throws Exception{
		
		UserDto user = userService.getUserProfile(jwt);
		
		List<Bug> bugs = bugService.getAllBugs(status);
		
		return new ResponseEntity<>(bugs, HttpStatus.OK);
	}
	
	@PutMapping("/{id}/user/{userId}/assigned")
	public ResponseEntity<Bug> assignedBugToUser(@PathVariable Long id, @PathVariable Long userId, @RequestHeader("Authorization") String jwt) throws Exception{
		
		UserDto user = userService.getUserProfile(jwt);
		
		Bug bugs = bugService.assignedToUser(userId, id);
		
		return new ResponseEntity<>(bugs, HttpStatus.OK);
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<Bug> updateBug(@PathVariable Long id, @RequestBody Bug req,@RequestHeader("Authorization") String jwt) throws Exception{
		
		UserDto user = userService.getUserProfile(jwt);
		
		Bug bugs = bugService.updateBug(id, req, user.getId());
		
		return new ResponseEntity<>(bugs, HttpStatus.OK);
	}
	
	@PutMapping("/{id}/complete")
	public ResponseEntity<Bug> completeBug(@PathVariable Long id) throws Exception{
		
		
		Bug bugs = bugService.completedBug(id);
		
		return new ResponseEntity<>(bugs, HttpStatus.OK);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteBug(@PathVariable Long id) throws Exception{
		
		bugService.deleteBug(id);
		
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
=======

	@Autowired
	private UserService userService;

	@Autowired
	private BugDao bugDao;

	@GetMapping("/gateway/test")
	@PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
	public ResponseEntity<?> testGateway() {
		return ResponseEntity.ok("bug service has been called");
	}

	@PostMapping("/debug-save")
	public ResponseEntity<?> debugSave() {
		Bug bug = new Bug();
		bug.setTitle("Debug");
		bug.setDescription("test bug");
		bug.setProjectId(1L);
//	    bug.setTags(Arrays.asList("t1", "t2"));
		bug.setStatus(BugStatus.ASSIGNED);
		bug.setCreatedAt(LocalDate.now());
		Bug saved = bugDao.save(bug);
		return ResponseEntity.ok(saved);
	}

	@GetMapping("/test")
	@PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
	public ResponseEntity<?> checking(@RequestHeader("Authorization") String jwt) {
		UserDto user = userService.getUserProfile(jwt);

		return ResponseEntity.ok(user.getRole());
	}

	@PostMapping("/add")
	@PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
	public ResponseEntity<Bug> createBug(@RequestBody BugRequestDto bugRequest,
			@RequestHeader("Authorization") String jwt) throws Exception {
		System.out.println("in create bug mwthod");
		UserDto user = userService.getUserProfile(jwt);

		Bug createdBug = bugService.createBug(bugRequest, user.getRole());

		return new ResponseEntity<>(createdBug, HttpStatus.CREATED);
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
	public ResponseEntity<?> getBugById(@PathVariable Long id, @RequestHeader("Authorization") String jwt)
			throws Exception {

		UserDto user = userService.getUserProfile(jwt);

		BugResponseDTO bug = bugService.getBugById(id);

		return new ResponseEntity<>(bug, HttpStatus.OK);
	}

	@GetMapping("/user")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<List<BugResponseDTO>> getAssignedUsersBug(@RequestParam(required = false) BugStatus status,
			@RequestHeader("Authorization") String jwt) throws Exception {

		UserDto user = userService.getUserProfile(jwt);

		List<BugResponseDTO> bugs = bugService.assignedUsersBug(user.getId(), status);

		return new ResponseEntity<>(bugs, HttpStatus.OK);
	}

	@GetMapping()
	@PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
	public ResponseEntity<List<BugResponseDTO>> getAllBug(@RequestParam(required = false) BugStatus status,
			@RequestHeader("Authorization") String jwt) throws Exception {

		List<BugResponseDTO> bugs = bugService.getAllBugs(status);

		return new ResponseEntity<>(bugs, HttpStatus.OK);
	}

	@PutMapping("/{id}/user/{userId}/assigned")
	@PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
	public ResponseEntity<Bug> assignedBugToUser(@PathVariable Long id, @PathVariable Long userId,
			@RequestHeader("Authorization") String jwt) throws Exception {

		Bug bugs = bugService.assignedToUser(userId, id);

		return new ResponseEntity<>(bugs, HttpStatus.OK);
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
	public ResponseEntity<?> updateBug(@PathVariable Long id, @RequestBody Bug req,
			@RequestHeader("Authorization") String jwt) throws Exception {

		UserDto user = userService.getUserProfile(jwt);

		BugResponseDTO bugs = bugService.updateBug(id, req, user.getId());

		return new ResponseEntity<>(bugs, HttpStatus.OK);
	}

	@PutMapping("/{id}/complete")
	@PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
	public ResponseEntity<Bug> completeBug(@PathVariable Long id) throws Exception {

		Bug bugs = bugService.completedBug(id);

		return new ResponseEntity<>(bugs, HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasAnyRole('ADMIN')")
	public ResponseEntity<Void> deleteBug(@PathVariable Long id) throws Exception {

		bugService.deleteBug(id);

		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@GetMapping("/counts")
	@PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
	public ResponseEntity<Map<String, Map<String, Long>>> getBugCounts() {
		Map<String, Map<String, Long>> response = new HashMap<>();
		response.put("priorityCounts", bugService.getBugCountByPriority());
		response.put("statusCounts", bugService.getBugCountByStatus());
		return ResponseEntity.ok(response);
	}


	@GetMapping("/project/{id}")
	@PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
	public ResponseEntity<List<BugResponseDTO>> getBugByProjectId(@PathVariable Long id) throws Exception {
	    List<BugResponseDTO> bugs = bugService.getAllBugsByProjectId(id);
	    return new ResponseEntity<>(bugs, HttpStatus.OK);
	}
	@GetMapping("/priority-count/{userId}")
	@PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
	public List<PriorityCountDTO> getPriorityCount(@PathVariable Long userId) {
	    return bugService.getBugCountByPriorityForUser(userId);
	}
	
	
//	@GetMapping("/status-counts")
//	@PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
//    public ResponseEntity<List<StatusCountDTO>> getBugCountsByStatus() {
//        List<StatusCountDTO> counts = bugService.getBugCountsByStatus();
//        return ResponseEntity.ok(counts);
//    }
//	
//	
//	@GetMapping("/status-counts/{userId}")
//	@PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
//	public ResponseEntity<List<StatusCountDTO>> getUserBugCountsByStatus(@PathVariable Long userId) {
//	    List<StatusCountDTO> counts = bugService.getUserBugCountsByStatus(userId);
//	    return ResponseEntity.ok(counts);
//	}
	
	
	@PutMapping("/{bugId}/status")
    public ResponseEntity<?> changeBugStatus(@PathVariable Long bugId, @RequestBody String statusStr) {
        BugStatus newStatus;
        try {
            newStatus = BugStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body("Invalid status value. Allowed: INPROGRESS, ASSIGNED, REASSIGNED, REVIEW, DONE");
        }
        try {
            Bug updatedBug = bugService.updateBugStatus(bugId, newStatus);
            return ResponseEntity.ok(updatedBug);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Bug not found with id: " + bugId);
        }
    }

>>>>>>> b5a9c0e (security changed for cors)
}
