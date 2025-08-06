package com.bug_submission_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping
@AllArgsConstructor
public class HomeController {

	@GetMapping("/submission")
	public ResponseEntity<?> homeController(){
		
		return ResponseEntity.ok("welcome to task submission service");
	}
}
