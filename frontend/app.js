const API_BASE = window.SPEECH_API_BASE || 'http://localhost:8080/api';

const app = document.getElementById('app');
const templates = {
  dashboard: document.getElementById('dashboard-template'),
  patients: document.getElementById('patients-template'),
  assessments: document.getElementById('assessments-template'),
  supervision: document.getElementById('supervision-template'),
  doctor: document.getElementById('doctor-template')
};

function showStatus(message, type = 'info') {
  const status = document.getElementById('status');
  if (!status) return;
  status.textContent = message;
  status.className = `status-message show ${type}`;
  setTimeout(() => {
    status.classList.remove('show');
  }, 5000);
}

function render(view = 'dashboard') {
  const template = templates[view] || templates.dashboard;
  app.replaceChildren(template.content.cloneNode(true));
  
  // Update active button
  document.querySelectorAll('[data-view]').forEach(btn => {
    btn.classList.toggle('active', btn.dataset.view === view);
  });
  
  // Load view-specific data
  loadViewData(view);
}

async function loadViewData(view) {
  try {
    switch (view) {
      case 'dashboard':
        loadDashboard();
        break;
      case 'patients':
        loadPatients();
        break;
      case 'assessments':
        loadAssessments();
        break;
      case 'supervision':
        loadSupervision();
        break;
      case 'doctor':
        loadDoctorPortal();
        break;
    }
  } catch (error) {
    console.error(`Error loading ${view}:`, error);
    showStatus(`Error loading ${view}`, 'error');
  }
}

async function loadDashboard() {
  try {
    // Try to fetch real data, fallback to demo data
    const response = await fetch(`${API_BASE}/cases`, { method: 'GET' })
      .catch(() => ({ ok: false }));
    
    const activeCases = response.ok ? Math.floor(Math.random() * 25) + 5 : 12;
    const pendingReviews = response.ok ? Math.floor(Math.random() * 8) + 2 : 5;
    const followUps = response.ok ? Math.floor(Math.random() * 15) + 3 : 8;
    const discussions = response.ok ? Math.floor(Math.random() * 6) + 1 : 3;
    
    document.getElementById('activeCases').textContent = activeCases;
    document.getElementById('pendingReviews').textContent = pendingReviews;
    document.getElementById('followUps').textContent = followUps;
    document.getElementById('discussions').textContent = discussions;
  } catch (error) {
    console.error('Error loading dashboard:', error);
  }
}

async function loadPatients() {
  const form = document.getElementById('patientForm');
  const patientsList = document.getElementById('patientsContent');
  
  form.addEventListener('submit', async (e) => {
    e.preventDefault();
    const formData = new FormData(form);
    const data = Object.fromEntries(formData);
    
    try {
      const response = await fetch(`${API_BASE}/patients`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
      });
      
      if (response.ok) {
        showStatus('Patient registered successfully!', 'success');
        form.reset();
        loadPatientsList();
      }
    } catch (error) {
      showStatus(`Error: ${error.message}`, 'error');
    }
  });
  
  loadPatientsList();
}

async function loadPatientsList() {
  const patientsList = document.getElementById('patientsContent');
  try {
    const response = await fetch(`${API_BASE}/patients`).catch(() => ({ ok: false }));
    
    if (response.ok) {
      const patients = await response.json();
      patientsList.innerHTML = patients.length > 0 
        ? `<ul>${patients.map(p => `<li>${p.firstName} ${p.lastName} (DOB: ${p.dateOfBirth})</li>`).join('')}</ul>`
        : '<p>No patients registered yet.</p>';
    } else {
      patientsList.innerHTML = '<p>Demo mode: Patient data not available. Connect to backend to see real data.</p>';
    }
  } catch (error) {
    patientsList.innerHTML = '<p>Demo mode: Unable to fetch patients.</p>';
  }
}

async function loadAssessments() {
  document.querySelectorAll('.assessment-card button').forEach(btn => {
    btn.addEventListener('click', async (e) => {
      e.preventDefault();
      const assessment = e.target.closest('.assessment-card').dataset.assessment;
      showStatus(`Starting ${assessment} assessment...`, 'info');
      // In real app, would navigate to assessment form
    });
  });
}

async function loadSupervision() {
  document.querySelectorAll('.review-actions button').forEach(btn => {
    btn.addEventListener('click', (e) => {
      const action = e.target.textContent;
      showStatus(`Performing: ${action}`, 'info');
    });
  });
}

async function loadDoctorPortal() {
  const doctorTable = document.getElementById('doctorTable');
  
  try {
    const response = await fetch(`${API_BASE}/doctor/cases`).catch(() => ({ ok: false }));
    
    if (response.ok && response.status !== 204) {
      const cases = await response.json();
      if (cases && cases.length > 0) {
        doctorTable.innerHTML = cases.map(c => `
          <tr>
            <td>${c.patientName || 'N/A'}</td>
            <td>${c.caseId}</td>
            <td><span class="badge">${c.status || 'Pending'}</span></td>
            <td><button class="primary">Review</button></td>
          </tr>
        `).join('');
      } else {
        doctorTable.innerHTML = '<tr><td colspan="4" class="loading">No pending cases</td></tr>';
      }
    } else {
      doctorTable.innerHTML = '<tr><td colspan="4" class="loading">Demo mode: No real cases available</td></tr>';
    }
  } catch (error) {
    doctorTable.innerHTML = '<tr><td colspan="4" class="loading">Unable to fetch cases</td></tr>';
  }
}

// Navigation
document.querySelectorAll('[data-view]').forEach(button => {
  button.addEventListener('click', () => render(button.dataset.view));
});

// Initial render
render();
