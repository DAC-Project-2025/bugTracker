package com.project.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
	
}
