package com.project_service.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project_service.model.Project;

public interface ProjectRepository extends JpaRepository<Project, Long> {

}
