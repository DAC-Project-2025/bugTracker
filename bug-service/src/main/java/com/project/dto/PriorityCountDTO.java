package com.project.dto;

import com.project.model.Priority;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PriorityCountDTO {
	private Priority priority;
    private Long count;
}
