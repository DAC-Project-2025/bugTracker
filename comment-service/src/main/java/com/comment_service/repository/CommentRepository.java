package com.comment_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.comment_service.model.Comment;

public interface CommentRepository extends	 JpaRepository<Comment, Long> {
	List<Comment> findByBugId(Long bugId);
}
