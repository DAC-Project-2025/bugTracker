package com.bug_submission_service.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.bug_submission_service.model.BugStatus;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskDto {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private String title;
	
	private String description;
	
	private String image;
	
	private Long assignedUserId;
	
	private List<String> tags = new ArrayList<>();
	
	@Enumerated(EnumType.STRING)
	private BugStatus status;
	
	private LocalDateTime deadLine;
	
	private LocalDateTime createdAt;
}
