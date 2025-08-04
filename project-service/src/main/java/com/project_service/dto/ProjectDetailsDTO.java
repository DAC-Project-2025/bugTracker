package com.project_service.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectDetailsDTO {
    private Long id;
    private String title;
    private String description;
    private String category;
    private String status;
    private String priority;
    private List<String> technologies;
    private LocalDate startDate;
    private LocalDate dueDate;
    private List<ProjectMemberDTO> teamMembers;
}
