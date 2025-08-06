package com.bugTracker.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileDTO {
	
	 	@NotBlank(message = "Name is required")
	    private String name;

	    @Email(message = "Email should be valid")
	    @NotBlank(message = "Email is required")
	    private String email;

	    @NotBlank(message = "Role is required")
	    private String role;
}
