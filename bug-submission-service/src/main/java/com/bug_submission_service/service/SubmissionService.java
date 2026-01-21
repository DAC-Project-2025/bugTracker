package com.bug_submission_service.service;

import java.util.List;

import com.bug_submission_service.model.Submission;

public interface SubmissionService {

	Submission submitTask(Long taskId, Long userId, String jwt) throws Exception;
	Submission getTaskSubmissionById(Long submissionId) throws Exception;
	
	List<Submission> getAllTaskSubmission();
	List<Submission> getTaskSubmissionByTaskId(Long taskId) throws Exception;
	
	Submission acceptDeclineSubmission(long id, String status) throws Exception;
	
}
