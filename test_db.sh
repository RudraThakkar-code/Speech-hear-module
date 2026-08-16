#!/bin/bash
cd backend
mvn clean test -Dtest=ClinicalInterpretationIntegrationTest || true
