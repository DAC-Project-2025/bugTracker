package com.attachment_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.attachment_service.dto.FileUploadResponseDTO;
import com.attachment_service.service.AttachmentService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/attachments")
@AllArgsConstructor
public class AttachmentController {
	 private final AttachmentService attachmentService;

	 @PostMapping("/upload")
	 public ResponseEntity<FileUploadResponseDTO> uploadFile(
	         @RequestParam("file") MultipartFile file,
	         @RequestParam("fileType") String fileType) {
	     return ResponseEntity.ok(attachmentService.uploadFile(file, fileType));
	 }
}
