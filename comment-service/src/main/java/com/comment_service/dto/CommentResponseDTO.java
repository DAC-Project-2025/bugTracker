package com.comment_service.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponseDTO {
	private Long id;
	private Long bugId;
	private Long userId;
	private String message;
	private LocalDateTime timestamp;
	private String userName;

}
