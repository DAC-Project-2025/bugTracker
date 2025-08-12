package com.attachment_service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Attachment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String fileUrl; // Cloudinary secure URL
	private String publicId; // For deletion
	private String type; // avatar / task-docs / etc.
	private String fileName; // original file name
	private String contentType; // image/png, application/pdf etc.
	private Long size; // file size in bytes

	private Long taskId; // foreign key to Task (optional)
	private Long userId; // foreign key to User (optional)
}
