const app = document.getElementById('app');
const templates = {
  dashboard: document.getElementById('dashboard-template'),
  patients: document.getElementById('patients-template'),
  assessments: document.getElementById('assessments-template'),
  supervision: document.getElementById('supervision-template'),
  doctor: document.getElementById('doctor-template')
};

function render(view = 'dashboard') {
  const template = templates[view] || templates.dashboard;
  app.replaceChildren(template.content.cloneNode(true));
}

document.querySelectorAll('[data-view]').forEach(button => {
  button.addEventListener('click', () => render(button.dataset.view));
});

render();
