package com.project.dto;

import java.time.LocalDate;
import java.util.List;

import com.project.model.BugStatus;
import com.project.model.Priority;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BugResponseDTO {
	private Long id;
	private Long reporter;
    private Long projectId;
    private String title;
    private String description;
    private Long assignedUserId;
    
    private String environment;
    private String browser;
    private String os;
    private List<String> files;
    private BugStatus status;
    private Priority priority;
    private LocalDate deadLine;
    private LocalDate createdAt;
}
