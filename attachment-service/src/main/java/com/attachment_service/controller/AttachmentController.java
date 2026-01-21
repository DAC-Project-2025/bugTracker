package com.attachment_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.attachment_service.dto.FileUploadResponseDTO;
import com.attachment_service.service.AttachmentService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/attachments")
public class AttachmentController {
	private final AttachmentService attachmentService;

	@GetMapping("test")
	public ResponseEntity<?> testGateway(){
		return ResponseEntity.ok("attachments has been called");
	}
	
	@PostMapping("/upload")
	public ResponseEntity<FileUploadResponseDTO> uploadFile(@RequestParam("file") MultipartFile file,
			@RequestParam("fileName") String fileName) {
		return ResponseEntity.ok(attachmentService.uploadFile(file, fileName));
	}
}
