<script setup lang="ts">
import Sidebar from '../components/Sidebar.vue'
</script>

<template>
  <div class="dashboard-shell">
    <Sidebar />

    <main class="dashboard-main">
      <section class="dashboard-panel">
        <p class="eyebrow">Frontend placeholder</p>
        <h1>Dashboard</h1>
        <p>
          The existing sidebar is now mounted behind a real route. Replace this area with the
          authenticated dashboard once those views are ready.
        </p>
      </section>
    </main>
  </div>
</template>

<style scoped>
.dashboard-shell {
  min-height: 100vh;
  background: var(--bg);
}

.dashboard-main {
  margin-left: 260px;
  min-height: 100vh;
  padding: 48px;
}

.dashboard-panel {
  max-width: 720px;
  border: 1px solid var(--border);
  border-radius: 28px;
  background: var(--surface);
  padding: 40px;
  box-shadow: var(--shadow-soft);
}

.eyebrow {
  margin: 0 0 12px;
  color: var(--text-muted);
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.12em;
  text-transform: uppercase;
}

h1 {
  margin: 0 0 12px;
  color: var(--text);
  font-size: 36px;
  line-height: 1.1;
}

p {
  margin: 0;
  color: var(--text-muted);
  font-size: 16px;
  line-height: 1.6;
}

@media (max-width: 900px) {
  .dashboard-main {
    margin-left: 0;
    padding: 24px;
  }
}
</style>
