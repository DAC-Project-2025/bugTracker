package com.project.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
	
	@GetMapping("/bugs")
	public ResponseEntity<String> getAssignedUsersBug(){
		
		
		return new ResponseEntity<>("Welcome to bug service!", HttpStatus.OK);
	}

}
