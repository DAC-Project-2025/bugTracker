package com.project_service.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectMemberDTO {
    private Long userId;
    private String userName;
    private String userEmail;
    private String userRole;
    private LocalDate assignedDate;
}
