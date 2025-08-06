package com.bugTracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequestDTO {
	private String email;
    private String password;
    private String name;
    private String role;
}
