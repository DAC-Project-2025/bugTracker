package com.project.dto;

import com.project.model.BugStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatusCountDTO {
	
	 private BugStatus status;
	    private Long count;

	
}
