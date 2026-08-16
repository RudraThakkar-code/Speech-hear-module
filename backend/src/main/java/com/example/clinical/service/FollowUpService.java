package com.example.clinical.service;

import com.example.clinical.dto.FollowUpRequest;
import com.example.clinical.dto.FollowUpResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FollowUpService {

    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public FollowUpResponse createOrReplace(UUID encounterId, FollowUpRequest request) {
        UUID followUpId = UUID.randomUUID();
        jdbcTemplate.update("""
                INSERT INTO follow_up (followup_id, encounter_id, previous_recommendations_met, current_status_summary)
                VALUES (?, ?, ?, ?)
                ON CONFLICT (encounter_id) DO UPDATE SET
                    previous_recommendations_met = EXCLUDED.previous_recommendations_met,
                    current_status_summary = EXCLUDED.current_status_summary,
                    updated_at = NOW()
                """, followUpId, encounterId, request.getPreviousRecommendationsMet(), request.getCurrentStatusSummary());

        return jdbcTemplate.queryForObject("""
                SELECT followup_id, encounter_id, previous_recommendations_met, current_status_summary
                FROM follow_up WHERE encounter_id = ?
                """, (rs, rowNum) -> FollowUpResponse.builder()
                .followUpId((UUID) rs.getObject("followup_id"))
                .encounterId((UUID) rs.getObject("encounter_id"))
                .previousRecommendationsMet(rs.getBoolean("previous_recommendations_met"))
                .currentStatusSummary(rs.getString("current_status_summary"))
                .build(), encounterId);
    }
}
