package com.example.clinical.service;
import com.example.clinical.domain.entity.*; import com.example.clinical.dto.*; import com.example.clinical.repository.*; import lombok.RequiredArgsConstructor; import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
@Service @RequiredArgsConstructor
public class ArticulationService {
 private final ArticulationProductionAttemptRepository attempts; private final ArticulationTargetLibraryRepository targets; private final EncounterRepository encounters; private final ArticulationAssessmentContextRepository contexts;
 @Transactional public ArticulationProductionResponse create(ArticulationProductionRequest r){
  ArticulationTargetLibrary t=targets.findById(r.targetId()).orElseThrow(()->new IllegalArgumentException("Articulation target not found")); Encounter e=encounters.findById(r.encounterId()).orElseThrow(()->new IllegalArgumentException("Encounter not found")); ArticulationProductionAttempt a=new ArticulationProductionAttempt(); a.setTarget(t); a.setEncounter(e); if(r.productionContextId()!=null)a.setProductionContext(contexts.findById(r.productionContextId()).orElseThrow(()->new IllegalArgumentException("Articulation context not found"))); a.setAttemptNumber(r.attemptNumber()); a.setProductionAccuracy(r.productionAccuracy()); a.setProducedPhoneme(r.producedPhoneme()); a.setClinicianObservation(r.clinicianObservation()); a.setProductionAudioUrl(r.productionAudioUrl()); return map(attempts.save(a));
 }
 @Transactional(readOnly=true) public ArticulationProductionResponse get(java.util.UUID id){return map(attempts.findById(id).orElseThrow(()->new IllegalArgumentException("Articulation production attempt not found")));}
 private ArticulationProductionResponse map(ArticulationProductionAttempt a){return new ArticulationProductionResponse(a.getId(),a.getTarget().getId(),a.getEncounter().getId(),a.getProductionContext()==null?null:a.getProductionContext().getId(),a.getAttemptNumber(),a.getProductionAccuracy(),a.getProducedPhoneme(),a.getClinicianObservation(),a.getProductionAudioUrl());}
}
