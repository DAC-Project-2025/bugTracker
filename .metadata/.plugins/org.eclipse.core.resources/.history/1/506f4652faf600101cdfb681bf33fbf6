package com.project.model;

<<<<<<< HEAD
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

=======
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.ElementCollection;
>>>>>>> b5a9c0e (security changed for cors)
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
<<<<<<< HEAD
import jakarta.persistence.Table;
=======
>>>>>>> b5a9c0e (security changed for cors)
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
<<<<<<< HEAD
@Table(name = "bug")
public class Bug {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private String title;
	
	private String description;
	
	private String image;
	
	private Long assignedUserId;
	
	private List<String> tags = new ArrayList<>();
	
	@Enumerated(EnumType.STRING)
	private BugStatus status;
	
	private LocalDateTime deadLine;
	
	private LocalDateTime createdAt;
=======
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
>>>>>>> b5a9c0e (security changed for cors)
	
	
	
}
