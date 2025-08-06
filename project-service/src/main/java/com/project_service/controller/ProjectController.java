package com.project_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project_service.dto.ProjectDetailsDTO;
import com.project_service.dto.ProjectRequestDTO;
import com.project_service.dto.ProjectResponseDTO;
import com.project_service.service.ProjectService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/projects")
@AllArgsConstructor
public class ProjectController {

	private final ProjectService projectService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ProjectResponseDTO> createProject(@RequestBody ProjectRequestDTO dto,
    		@RequestHeader("Authorization") String jwt
    		) {
        ProjectResponseDTO response = projectService.createProject(dto,jwt);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
    public ResponseEntity<ProjectDetailsDTO> getProjectById(@PathVariable Long id) {
        ProjectDetailsDTO details = projectService.getProjectById(id);
        return ResponseEntity.ok(details);
    }
}
