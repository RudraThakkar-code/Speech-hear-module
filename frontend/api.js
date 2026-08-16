const API_BASE = window.SPEECH_API_BASE || 'http://localhost:8080/api/v1';

export async function apiRequest(path, options = {}) {
  const response = await fetch(`${API_BASE}${path}`, {
    headers: { 'Content-Type': 'application/json', ...(options.headers || {}) },
    ...options
  });
  if (!response.ok) {
    const text = await response.text();
    throw new Error(text || `Request failed: ${response.status}`);
  }
  return response.status === 204 ? null : response.json();
}

export const api = {
  patient: patientId => apiRequest(`/patients/${patientId}`),
  encounter: encounterId => apiRequest(`/encounters/${encounterId}`),
  evidence: encounterId => apiRequest(`/encounters/${encounterId}/evidence-summary`),
  interpretation: interpretationId => apiRequest(`/clinical-interpretations/${interpretationId}`),
  reports: caseId => apiRequest(`/cases/${caseId}/reports`),
  notifications: userId => apiRequest(`/notifications/users/${userId}`),
  doctorRecommendations: caseId => apiRequest(`/doctor/cases/${caseId}/recommendations`)
};
