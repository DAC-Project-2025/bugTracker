package com.project_service.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project_service.model.ProjectMember;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {

}
