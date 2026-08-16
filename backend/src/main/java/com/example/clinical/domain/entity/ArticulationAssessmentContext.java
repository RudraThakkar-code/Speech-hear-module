package com.example.clinical.domain.entity;
import jakarta.persistence.*;
import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.util.UUID;
@Entity @Table(name="articulation_assessment_context") @Getter @Setter @NoArgsConstructor
public class ArticulationAssessmentContext {
 @Id @GeneratedValue(strategy=GenerationType.UUID) @Column(name="context_id",nullable=false,updatable=false) private UUID id;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="encounter_id",nullable=false) private Encounter encounter;
 @Column(name="context_description",nullable=false) private String contextDescription;
}
