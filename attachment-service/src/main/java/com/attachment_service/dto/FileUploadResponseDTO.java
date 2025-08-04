package com.attachment_service.dto;

import lombok.Data;

@Data
public class FileUploadResponseDTO {
	private String fileUrl;
	private String publicId;
	private String type;
}
