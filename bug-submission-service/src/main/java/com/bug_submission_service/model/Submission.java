package com.bug_submission_service.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Submission {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long Id;
	private Long taskId;
	
	private Long userId;
	
	private String status="PENDING";
	
	private LocalDateTime submissiontime;
	
	
	
}
