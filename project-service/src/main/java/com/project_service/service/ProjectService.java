package com.project_service.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project_service.dao.ProjectRepository;
import com.project_service.dto.ProjectDetailsDTO;
import com.project_service.dto.ProjectMemberDTO;
import com.project_service.dto.ProjectRequestDTO;
import com.project_service.dto.ProjectResponseDTO;
import com.project_service.dto.UserDTO;
import com.project_service.feign.UserClient;
import com.project_service.model.Project;
import com.project_service.model.ProjectMember;

import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class ProjectService {

	 private final ProjectRepository projectRepo;
	 private final UserClient userClient;
	 private final ModelMapper modelMapper;

	 public ProjectResponseDTO createProject(ProjectRequestDTO dto ,String jwt) {
	        // Map basic project fields
		 Project project = modelMapper.map(dto, Project.class);
		 System.out.println("Mapped description: " + project.getDescription());
		 
		 
//		 	Project project = new Project();
//	        project.setTitle(dto.getTitle());
//	        project.setDescription(dto.getDescription());
//	        project.setCategory(dto.getCategory());
//	        project.setStatus(dto.getStatus());
//	        project.setPriority(dto.getPriority());
//	        project.setTechnologies(dto.getTechnologies());
//	        project.setStartDate(dto.getStartDate());
//	        project.setDueDate(dto.getDueDate());
		 
	        // Manually set team members using Feign
		 List<ProjectMember> members = new ArrayList<>();

	        for (Long userId : dto.getTeamMemberIds()) {
	            UserDTO userDto = userClient.getUserById(userId, jwt);
	        	
//-------------temporary data later use above method -----------------
//	        	UserDTO userDto = new UserDTO();
//	        	userDto.setId(userId);
//	        	userDto.setName("Dummy User " + userId);
//	        	userDto.setEmail("dummy" + userId + "@example.com");
//	        	userDto.setRole("DEVELOPER");
//---------------------------------------------------------------------
	            ProjectMember member = new ProjectMember();
	            member.setProject(project);
	            member.setUserId(userDto.getId());
	            member.setUserName(userDto.getName());
	            member.setUserEmail(userDto.getEmail());
	            member.setUserRole(userDto.getRole());
	            member.setAssignedDate(LocalDate.now());

	            members.add(member);
	        }

	        project.setTeamMembers(members);

	        // Save project with members
	        projectRepo.save(project);

	        // Prepare response DTO (can use modelMapper again)
	        ProjectResponseDTO response = new ProjectResponseDTO();
	        response.setId(project.getId());
	        response.setTitle(project.getTitle());
	        response.setStatus(project.getStatus());
	        response.setDescription(project.getDescription());
	        response.setTeamMembers(
	            project.getTeamMembers().stream()
	                .map(ProjectMember::getUserName)
	                .toList()
	        );

	        return response;
	    }
	 
	 
	 public ProjectDetailsDTO getProjectById(Long id) {
		    Project project = projectRepo.findById(id)
		        .orElseThrow(() -> new RuntimeException("Project not found"));

		    ProjectDetailsDTO details = modelMapper.map(project, ProjectDetailsDTO.class);

		    // Map members manually since ModelMapper won't map nested lists cleanly by default
		    List<ProjectMemberDTO> memberDTOs = project.getTeamMembers().stream()
		        .map(member -> modelMapper.map(member, ProjectMemberDTO.class))
		        .toList();

		    details.setTeamMembers(memberDTOs);
		    return details;
		}
}
