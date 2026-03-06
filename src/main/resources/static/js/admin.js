/* ============================================================
   QUIZZY – Admin panel JS
   ============================================================ */

(function () {
  'use strict';

  // ── Modals ────────────────────────────────────────────────────
  document.addEventListener('DOMContentLoaded', () => {
    // Ouvrir modals
    document.querySelectorAll('[data-modal-open]').forEach(btn => {
      btn.addEventListener('click', () => {
        const id = btn.dataset.modalOpen;
        openModal(id);

        // Pré-remplir données si edit
        const editId   = btn.dataset.editId;
        const editName = btn.dataset.editName;
        if (editId && editName) {
          const form = document.getElementById(id)?.querySelector('form');
          if (form) {
            const idInput   = form.querySelector('[name="id"]');
            const nameInput = form.querySelector('[name="name"]');
            if (idInput)   idInput.value   = editId;
            if (nameInput) nameInput.value = editName;
            // Mise à jour de l'action du formulaire
            form.action = form.dataset.baseAction?.replace('{id}', editId) ?? form.action;
          }
        }
      });
    });

    // Fermer modals
    document.querySelectorAll('[data-modal-close], .modal-overlay').forEach(el => {
      el.addEventListener('click', e => {
        if (e.target === el || el.hasAttribute('data-modal-close')) {
          el.closest('.modal-overlay')?.classList.remove('open');
        }
      });
    });

    // Echap
    document.addEventListener('keydown', e => {
      if (e.key === 'Escape') {
        document.querySelectorAll('.modal-overlay.open').forEach(m =>
          m.classList.remove('open'));
      }
    });

    // Confirmations de suppression
    document.querySelectorAll('[data-confirm]').forEach(btn => {
      btn.addEventListener('click', e => {
        const msg = btn.dataset.confirm || 'Confirmer la suppression ?';
        if (!confirm(msg)) e.preventDefault();
      });
    });

    // Options quiz
    initOptionBuilder();

    // Filtre/recherche dans les tableaux
    initTableSearch();

    // Auto-dismiss alerts
    document.querySelectorAll('.alert[data-auto-dismiss]').forEach(alert => {
      setTimeout(() => {
        alert.style.opacity = '0';
        alert.style.transition = 'opacity .4s';
        setTimeout(() => alert.remove(), 400);
      }, 4000);
    });
  });

  function openModal(id) {
    const overlay = document.getElementById(id);
    if (overlay) overlay.classList.add('open');
  }

  // ── Option builder (question form) ────────────────────────────
  function initOptionBuilder() {
    const addBtn = document.getElementById('add-option');
    if (!addBtn) return;

    const container = document.getElementById('options-container');
    let count = container?.querySelectorAll('.option-row').length ?? 0;

    addBtn.addEventListener('click', () => {
      if (count >= 6) return;
      count++;
      const letters = ['A', 'B', 'C', 'D', 'E', 'F'];
      const row = document.createElement('div');
      row.className = 'option-row';
      row.innerHTML = `
        <input type="text" class="form-input" name="optionTexts"
               placeholder="Option ${letters[count - 1]}" required>
        <label class="option-row__correct">
          <input type="checkbox" name="correctOptions" value="${count - 1}">
          <span class="material-symbols-outlined">check</span> Correcte
        </label>
        <button type="button" class="btn btn--ghost btn--sm remove-option"><span class="material-symbols-outlined">close</span></button>
      `;
      container.appendChild(row);

      row.querySelector('.remove-option').addEventListener('click', () => {
        row.remove();
        count--;
      });
    });

    // Supprimer lignes existantes
    document.querySelectorAll('.remove-option').forEach(btn => {
      btn.addEventListener('click', () => {
        btn.closest('.option-row').remove();
        count--;
      });
    });
  }

  // ── Table search ──────────────────────────────────────────────
  function initTableSearch() {
    const searchInput = document.getElementById('table-search');
    if (!searchInput) return;

    searchInput.addEventListener('input', () => {
      const q = searchInput.value.toLowerCase().trim();
      const rows = document.querySelectorAll('tbody tr');
      rows.forEach(row => {
        const text = row.textContent.toLowerCase();
        row.style.display = text.includes(q) ? '' : 'none';
      });
    });
  }




  // Pré-remplissage modal modifier question
document.querySelectorAll('[data-modal-open="modal-edit-question"]').forEach(btn => {
  btn.addEventListener('click', () => {
    const id = btn.dataset.id;
    document.getElementById('form-edit-question').action = `/admin/questions/update/${id}`;
    document.getElementById('edit-questionText').value = btn.dataset.text || '';
    document.getElementById('edit-difficulty').value = btn.dataset.difficulty || 'MEDIUM';
    const catSelect = document.getElementById('edit-categoryId');
    if (catSelect && btn.dataset.category) {
      catSelect.value = btn.dataset.category;
    }
  });
});

// Pré-remplissage modal modifier utilisateur
document.querySelectorAll('[data-modal-open="modal-edit-user"]').forEach(btn => {
  btn.addEventListener('click', () => {
    const id = btn.dataset.id;
    document.getElementById('form-edit-user').action = `/admin/users/update/${id}`;
    document.getElementById('edit-username').value = btn.dataset.username || '';
    document.getElementById('edit-email').value = btn.dataset.email || '';
    const roleSelect = document.getElementById('edit-role');
    if (roleSelect && btn.dataset.role) {
      roleSelect.value = btn.dataset.role;
    }
  });
});

})();
