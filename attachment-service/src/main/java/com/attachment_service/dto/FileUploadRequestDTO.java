package com.attachment_service.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileUploadRequestDTO {
	private MultipartFile file;
    private String fileType; // avatar, document, etc.

}
