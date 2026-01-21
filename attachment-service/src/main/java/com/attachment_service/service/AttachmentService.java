package com.attachment_service.service;

import org.springframework.web.multipart.MultipartFile;

import com.attachment_service.dto.FileUploadResponseDTO;

public interface AttachmentService {
	FileUploadResponseDTO uploadFile(MultipartFile file, String type);
}
