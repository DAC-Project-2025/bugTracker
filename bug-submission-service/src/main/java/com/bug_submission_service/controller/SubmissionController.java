package com.bug_submission_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bug_submission_service.dto.UserDto;
import com.bug_submission_service.model.Submission;
import com.bug_submission_service.service.SubmissionService;
import com.bug_submission_service.service.TaskService;
import com.bug_submission_service.service.UserService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/submission")
@AllArgsConstructor
public class SubmissionController {

	private final SubmissionService submissionService;
	
	private final UserService userService;
	private final TaskService taskService;
	
	@PostMapping 
	public ResponseEntity<Submission>submitTask(@RequestParam Long id, 
			@RequestHeader("Autharization")String jwt)throws Exception{
		
		UserDto user = userService.getUserProfile(jwt);
		Submission submission = submissionService.submitTask(id, user.getId(), jwt);
		
		return new ResponseEntity<>(submission, HttpStatus.CREATED);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Submission>getSubmissionById(
			@PathVariable Long id, 
			@RequestHeader("Autharization")String jwt)throws Exception{
		
		UserDto user = userService.getUserProfile(jwt);
		Submission submission = submissionService.getTaskSubmissionById(id);
		
		return new ResponseEntity<>(submission, HttpStatus.FOUND);
	}
	@GetMapping 
	public ResponseEntity<List<Submission>>getAllSubmissions(
			@RequestHeader("Autharization")String jwt)throws Exception{
		
		UserDto user = userService.getUserProfile(jwt);
		List<Submission> submissions = submissionService.getAllTaskSubmission();
		
		return new ResponseEntity<>(submissions, HttpStatus.FOUND);
	}
	
	@GetMapping("/task/{taskId}")
	public ResponseEntity<List<Submission>>getAllSubmissions(
			@PathVariable Long taskId,
			@RequestHeader("Autharization")String jwt)throws Exception{
		
		UserDto user = userService.getUserProfile(jwt);
		List<Submission> submissions = submissionService.getTaskSubmissionByTaskId(taskId);
		
		return new ResponseEntity<>(submissions, HttpStatus.FOUND);
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<Submission>acceptOrDeclineSubmission(
			@PathVariable Long id,
			@RequestParam("status") String status,
			@RequestHeader("Autharization")String jwt)throws Exception{
		
		UserDto user = userService.getUserProfile(jwt);
		Submission submission = submissionService.acceptDeclineSubmission(id, status);
		
		return new ResponseEntity<>(submission, HttpStatus.FOUND);
	}
	
}
