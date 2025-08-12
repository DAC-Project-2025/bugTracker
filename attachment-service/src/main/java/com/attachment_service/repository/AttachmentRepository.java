package com.attachment_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.attachment_service.model.Attachment;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

}
