package com.bug_submission_service.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bug_submission_service.dao.SubmissionDao;
import com.bug_submission_service.dto.TaskDto;
import com.bug_submission_service.model.Submission;

import lombok.AllArgsConstructor;


@Service
@Transactional
@AllArgsConstructor
public class SubmissionServiceImpl implements SubmissionService {
	
	private final SubmissionDao submissionDao;
	private final TaskService taskService; 

	@Override
	public Submission submitTask(Long taskId, Long userId, String jwt) throws Exception {
		// TODO Auto-generated method stub
		TaskDto task = taskService.getBugById(taskId,jwt);
		if(task!=null) {
			Submission submission = new Submission();
			submission.setTaskId(taskId);
			submission.setUserId(userId);
			submission.setSubmissiontime(LocalDateTime.now());
			
			return submissionDao.save(submission);
		}
		throw new Exception("NO Bug found with given id"+taskId);
	}

	@Override
	public Submission getTaskSubmissionById(Long submissionId) throws Exception {
		
		
		return submissionDao.findById(submissionId)
				.orElseThrow(()-> new Exception("task submission is not found with id "+ submissionId));
	}

	@Override
	public List<Submission> getAllTaskSubmission() {
	
		return submissionDao.findAll();
	}

	@Override
	public List<Submission> getTaskSubmissionByTaskId(Long taskId) throws Exception {

		return submissionDao.findByTaskId(taskId);
	}

	@Override
	public Submission acceptDeclineSubmission(long id, String status) throws Exception {
		Submission submission = getTaskSubmissionById(id);
		submission.setStatus(status);
		
		if(status.equals("ACCEPT")) {
			taskService.completeBug(submission.getTaskId());
		}
		
		return submissionDao.save(submission);
	}

}
