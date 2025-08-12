package com.project.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.project.model.Priority;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BugRequestDto {
	private Long reporter;
    private Long projectId;
    private String title;
    private String description;
    private Long assignedUserId;
    private LocalDate deadLine;
    private List<String> files;
    private String os;
    private String environment;
    private Priority priority;
    
    private String browser;
    
    
    
}




