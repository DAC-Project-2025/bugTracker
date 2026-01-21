package com.project.service;

import java.util.List;
import java.util.Map;

import com.project.dto.BugRequestDto;
import com.project.dto.BugResponseDTO;
import com.project.dto.PriorityCountDTO;
import com.project.model.Bug;
import com.project.model.BugStatus;

public interface BugService {

    /* ================= CREATE ================= */

    Bug createBug(BugRequestDto bugRequest, String requestedRole) throws Exception;

    /* ================= READ ================= */

    BugResponseDTO getBugById(Long id) throws Exception;

    List<BugResponseDTO> getAllBugs(BugStatus status);

    List<BugResponseDTO> assignedUsersBug(Long userId, BugStatus status);

    List<BugResponseDTO> getAllBugsByProjectId(Long projectId);

    /* ================= UPDATE ================= */

    BugResponseDTO updateBug(Long id, Bug updatedBug, Long userId) throws Exception;

    Bug assignedToUser(Long userId, Long taskId) throws Exception;

    Bug completedBug(Long bugId) throws Exception;

    Bug updateBugStatus(Long bugId, BugStatus newStatus);

    /* ================= DELETE ================= */

    void deleteBug(Long id) throws Exception;

    /* ================= STATS ================= */

    Map<String, Long> getBugCountByStatus();

    Map<String, Long> getBugCountByPriority();

    List<PriorityCountDTO> getBugCountByPriorityForUser(Long userId);
}
