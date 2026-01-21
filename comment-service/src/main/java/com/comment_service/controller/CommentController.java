package com.comment_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.comment_service.dto.CommentRequestDTO;
import com.comment_service.dto.CommentResponseDTO;
import com.comment_service.service.CommentService;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping("/test")
	@PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
	public ResponseEntity<?> testGateway(){
		return ResponseEntity.ok("comments has been called");
	}
    
    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
    public ResponseEntity<CommentResponseDTO> addComment(@RequestBody CommentRequestDTO dto) {
        return ResponseEntity.ok(commentService.addComment(dto));
    }

    @GetMapping("/bug/{bugId}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
    public ResponseEntity<List<CommentResponseDTO>> getCommentsByTask(@PathVariable Long bugId) {
        return ResponseEntity.ok(commentService.getCommentsByTask(bugId));
    }
}
