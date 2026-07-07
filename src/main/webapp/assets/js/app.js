/* Clínica UPN — interacciones base del shell */
(function () {
  'use strict';

  // Menú lateral en móvil
  function bindSidebar() {
    var toggle = document.querySelector('.topbar__toggle');
    var overlay = document.querySelector('.app-overlay');
    if (toggle) toggle.addEventListener('click', function () {
      document.body.classList.toggle('nav-open');
    });
    if (overlay) overlay.addEventListener('click', function () {
      document.body.classList.remove('nav-open');
    });
    document.addEventListener('keydown', function (e) {
      if (e.key === 'Escape') document.body.classList.remove('nav-open');
    });
  }

  // Marca el enlace del menú que corresponde a la página actual
  function markActive() {
    var current = (location.pathname.split('/').pop() || '').toLowerCase();
    document.querySelectorAll('.app-nav a').forEach(function (a) {
      var href = (a.getAttribute('href') || '').toLowerCase();
      if (href && current && href.indexOf(current) !== -1) a.classList.add('is-active');
    });
  }

  document.addEventListener('DOMContentLoaded', function () {
    bindSidebar();
    markActive();
  });
})();

/* Modales (delegación de eventos) — FASE 3 */
(function () {
  'use strict';
  function closeOpen() {
    var open = document.querySelector('.c-modal.is-open');
    if (open) open.classList.remove('is-open');
  }
  document.addEventListener('click', function (e) {
    var opener = e.target.closest('[data-modal-open]');
    if (opener) {
      var m = document.getElementById(opener.getAttribute('data-modal-open'));
      if (m) m.classList.add('is-open');
      return;
    }
    if (e.target.closest('[data-modal-close]') || e.target.classList.contains('c-modal')) {
      closeOpen();
    }
  });
  document.addEventListener('keydown', function (e) {
    if (e.key === 'Escape') closeOpen();
  });
})();