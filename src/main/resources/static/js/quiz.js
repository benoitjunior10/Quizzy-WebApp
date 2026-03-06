/* ============================================================
   QUIZZY – Quiz engine
   ============================================================ */

(function () {
  'use strict';

  // ── State ────────────────────────────────────────────────────
  const state = {
    current: 0,
    answers: {}, // questionId → Set of optionIds
    startTime: Date.now(),
    timerEl: null,
    timerInterval: null,
    totalQuestions: 0,
  };

  // ── Init ─────────────────────────────────────────────────────
  document.addEventListener('DOMContentLoaded', () => {
    const questions = document.querySelectorAll('.question-slide');
    if (!questions.length) return;

    state.totalQuestions = questions.length;
    startTimer();
    showQuestion(0);
    buildDots();

    // Boutons nav
    document.getElementById('btn-prev')?.addEventListener('click', () => navigate(-1));
    document.getElementById('btn-next')?.addEventListener('click', () => navigate(1));
    document.getElementById('btn-submit')?.addEventListener('click', submitQuiz);

    // Option selection
    document.addEventListener('change', handleOptionChange);
  });

  // ── Timer ────────────────────────────────────────────────────
  function startTimer() {
    state.timerEl = document.getElementById('quiz-timer');
    if (!state.timerEl) return;

    state.timerInterval = setInterval(() => {
      const elapsed = Math.floor((Date.now() - state.startTime) / 1000);
      state.timerEl.textContent = formatTime(elapsed);

      if (elapsed > 1800) state.timerEl.classList.add('warning');
      if (elapsed > 3000) {
        state.timerEl.classList.remove('warning');
        state.timerEl.classList.add('danger');
      }
    }, 1000);
  }

  function formatTime(sec) {
    const m = Math.floor(sec / 60).toString().padStart(2, '0');
    const s = (sec % 60).toString().padStart(2, '0');
    return `${m}:${s}`;
  }

  function getElapsedSeconds() {
    return Math.floor((Date.now() - state.startTime) / 1000);
  }

  // ── Navigation ───────────────────────────────────────────────
  function navigate(dir) {
    const next = state.current + dir;
    if (next < 0 || next >= state.totalQuestions) return;
    showQuestion(next);
  }

  function showQuestion(idx) {
    const slides = document.querySelectorAll('.question-slide');
    slides.forEach((s, i) => {
      s.classList.toggle('active', i === idx);
    });
    state.current = idx;
    updateDots();
    updateNavButtons();
    updateProgress();
  }

  function updateProgress() {
    const pct = ((state.current + 1) / state.totalQuestions) * 100;
    const bar = document.getElementById('progress-bar');
    if (bar) bar.style.width = pct + '%';

    const counter = document.getElementById('q-counter');
    if (counter) counter.textContent =
      `Question ${state.current + 1} / ${state.totalQuestions}`;
  }

  function updateNavButtons() {
    const prev = document.getElementById('btn-prev');
    const next = document.getElementById('btn-next');
    const submit = document.getElementById('btn-submit');

    if (prev) prev.disabled = state.current === 0;
    if (next) next.style.display =
      state.current < state.totalQuestions - 1 ? 'inline-flex' : 'none';
    if (submit) submit.style.display =
      state.current === state.totalQuestions - 1 ? 'inline-flex' : 'none';
  }

  // ── Dots ─────────────────────────────────────────────────────
  function buildDots() {
    const container = document.getElementById('quiz-dots');
    if (!container) return;

    container.innerHTML = '';
    for (let i = 0; i < state.totalQuestions; i++) {
      const btn = document.createElement('button');
      btn.className = 'quiz-dot';
      btn.setAttribute('aria-label', `Question ${i + 1}`);
      btn.addEventListener('click', () => showQuestion(i));
      container.appendChild(btn);
    }
    updateDots();
  }

  function updateDots() {
    const dots = document.querySelectorAll('.quiz-dot');
    dots.forEach((d, i) => {
      d.classList.toggle('active', i === state.current);
      d.classList.toggle('answered',
        state.answers[getQuestionId(i)] &&
        state.answers[getQuestionId(i)].size > 0);
    });
  }

  function getQuestionId(idx) {
    const slide = document.querySelectorAll('.question-slide')[idx];
    return slide?.dataset.questionId;
  }

  // ── Option selection ─────────────────────────────────────────
  function handleOptionChange(e) {
    const input = e.target;
    if (!input.classList.contains('option-input')) return;

    const qId = input.dataset.questionId;
    if (!state.answers[qId]) state.answers[qId] = new Set();

    if (input.type === 'radio') {
      // Décocher les autres labels visuellement
      const slide = input.closest('.question-slide');
      slide.querySelectorAll('.option-label').forEach(l =>
        l.classList.remove('selected'));
      state.answers[qId] = new Set([input.value]);
    } else {
      if (input.checked) state.answers[qId].add(input.value);
      else              state.answers[qId].delete(input.value);
    }

    // Marquer visuellement la sélection
    const label = input.closest('.option-label');
    label.classList.toggle('selected', input.checked);
    updateDots();
  }

  // ── Submit ───────────────────────────────────────────────────
  function submitQuiz() {
    clearInterval(state.timerInterval);
    const duration = getElapsedSeconds();

    // Collecter tous les optionIds sélectionnés
    const allSelected = [];
    Object.values(state.answers).forEach(set => {
      set.forEach(id => allSelected.push(id));
    });

    // Remplir les inputs cachés
    const form = document.getElementById('quiz-form');
    if (!form) return;

    // Vider les inputs précédents
    form.querySelectorAll('.hidden-option').forEach(el => el.remove());

    allSelected.forEach(id => {
      const input = document.createElement('input');
      input.type = 'hidden';
      input.name = 'selectedOptionIds';
      input.value = id;
      input.className = 'hidden-option';
      form.appendChild(input);
    });

    document.getElementById('hidden-duration').value = duration;
    form.submit();
  }
})();
