package com.example.clinical.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ArticulationContextResponse {
    private UUID id;
    private UUID encounterId;
    private String contextDescription;
}
