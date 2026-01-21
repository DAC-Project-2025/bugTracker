package com.project.model;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Bug {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	private Long reporter;
    private Long projectId;
    private String title;
    private String description;
    private Long assignedUserId;
    
    private String environment;
    private String browser;
    private String os;
    @ElementCollection
    private List<String> files;

    @Enumerated(EnumType.STRING)
    private BugStatus status;
    @Enumerated(EnumType.STRING)
    private Priority priority;
    private LocalDate deadLine;
    private LocalDate createdAt;

	
	
	
}
