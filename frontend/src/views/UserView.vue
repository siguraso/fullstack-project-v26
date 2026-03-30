<script setup lang="ts">
import Sidebar from '../components/Sidebar.vue'
import UserPage from './user/UserPage.vue'
</script>

<template>
  <div class="dashboard-shell">
    <Sidebar />

    <main class="dashboard-main">
      <UserPage />
    </main>
  </div>
</template>

<style scoped>
.dashboard-shell {
  min-height: 100vh;
  background: var(--bg);
}

.dashboard-main {
  margin-left: var(--sidebar-width, 220px);
  min-height: 100vh;
  padding: 48px;
}

@media (max-width: 900px) {
  .dashboard-main {
    margin-left: 0;
    padding: 24px;
  }
}
</style>
