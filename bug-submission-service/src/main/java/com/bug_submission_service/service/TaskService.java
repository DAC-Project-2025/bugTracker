package com.bug_submission_service.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import com.bug_submission_service.dto.TaskDto;

@FeignClient(name = "Submission-service", url = "http://localhost:8082/")
public interface TaskService {
//put method from task service getTaskTyId time 28
	@GetMapping("/api/bugs/{id}")
	public TaskDto getBugById(@PathVariable Long id, @RequestHeader("Authorization") String jwt ) throws Exception;
	@PutMapping("/api/bugs/{id}/complete")
	public TaskDto completeBug(@PathVariable Long id) throws Exception;

}
