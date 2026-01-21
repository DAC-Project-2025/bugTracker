package com.bug_submission_service.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bug_submission_service.model.Submission;

public interface SubmissionDao extends JpaRepository<Submission, Long> {

	List<Submission> findByTaskId(Long taskid);
}
