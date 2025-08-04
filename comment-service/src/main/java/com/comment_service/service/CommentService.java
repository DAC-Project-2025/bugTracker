package com.comment_service.service;

import java.util.List;

import com.comment_service.dto.CommentRequestDTO;
import com.comment_service.dto.CommentResponseDTO;

public interface CommentService {
	public CommentResponseDTO addComment(CommentRequestDTO dto);
	public List<CommentResponseDTO> getCommentsByTask(Long taskId);
}
