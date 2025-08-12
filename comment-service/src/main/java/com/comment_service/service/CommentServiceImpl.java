package com.comment_service.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.comment_service.dto.CommentRequestDTO;
import com.comment_service.dto.CommentResponseDTO;
import com.comment_service.feign.UserClient;
import com.comment_service.model.Comment;
import com.comment_service.repository.CommentRepository;

import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class CommentServiceImpl implements CommentService {

	private final CommentRepository commentRepository;
	// Remove userClient for now or comment it out
	 private final UserClient userClient;
	private final ModelMapper modelMapper;

//	// Temporary dummy user map
//	private static final Map<Long, String> userMap = Map.of(
//		1L, "Akash Kamble",
//		2L, "John Doe",
//		3L, "Jane Smith"
//	);

	
	/*
	 * Uncomment the UserClient dependency
		Replace the .setUserName(...) lines with:
		response.setUserName(userClient.getUserNameById(saved.getUserId()));
	 */
	@Override
	public CommentResponseDTO addComment(CommentRequestDTO dto) {
		Comment comment = modelMapper.map(dto, Comment.class);
		comment.setTimestamp(LocalDateTime.now());

		Comment saved = commentRepository.save(comment);

		CommentResponseDTO response = modelMapper.map(saved, CommentResponseDTO.class);

		// Replace Feign call with mock name
		response.setUserName(userClient.getUserName(saved.getUserId()));

		return response;
	}

	@Override
	public List<CommentResponseDTO> getCommentsByTask(Long bugId) {
		List<Comment> comments = commentRepository.findByBugId(bugId);

		return comments.stream().map(comment -> {
			CommentResponseDTO dto = modelMapper.map(comment, CommentResponseDTO.class);
			dto.setUserName(userClient.getUserName(comment.getUserId()));
			return dto;
		}).collect(Collectors.toList());
	}
}
