package com.comment_service.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentRequestDTO {

	 	@NotBlank(message = "Message cannot be blank")
	    private String message;

	    @NotNull(message = "Bug ID is required")
	    private Long bugId;

	    @NotNull(message = "User ID is required")
	    private Long userId;
}
