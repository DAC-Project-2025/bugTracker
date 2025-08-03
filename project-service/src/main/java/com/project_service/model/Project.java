package com.project_service.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Project {

		@Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    private String title;
	    private String description;
	    private String category;
	    private String status;
	    private String priority;

	    @ElementCollection
	    private List<String> technologies;

	    private LocalDate startDate;
	    private LocalDate dueDate;

	    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
	    private List<ProjectMember> teamMembers = new ArrayList<>();
}
