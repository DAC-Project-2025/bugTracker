package com.attachment_service.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.attachment_service.dto.FileUploadResponseDTO;
import com.attachment_service.model.Attachment;
import com.attachment_service.repository.AttachmentRepository;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {

	private final Cloudinary cloudinary;
	private final AttachmentRepository attachmentRepository;
	private final ModelMapper modelMapper;

	@Override
	public FileUploadResponseDTO uploadFile(MultipartFile file, String type) {
		try {
			@SuppressWarnings("unchecked")
			Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(),
					ObjectUtils.asMap("resource_type", "auto", "folder", type));

			// Map to entity
			Attachment attachment = new Attachment();
			attachment.setFileUrl((String) uploadResult.get("secure_url"));
			attachment.setPublicId((String) uploadResult.get("public_id"));
			attachment.setType(type);

			// Save to DB
			Attachment saved = attachmentRepository.save(attachment);

			// Map to response DTO
			return modelMapper.map(saved, FileUploadResponseDTO.class);

		} catch (IOException e) {
			throw new RuntimeException("File upload failed", e);
		}
	}
}
