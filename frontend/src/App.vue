<script lang="ts" setup>
import Sidebar from './components/Sidebar.vue'
import DashboardView from './views/dashboard/DashboardView.vue'
</script>

<template>
  <div class="app-shell">
    <Sidebar />
    <main class="main-content">
      <DashboardView />
    </main>
  </div>
</template>

<style scoped>
.app-shell {
  --sidebar-width: max(15vw, 220px);
  min-height: 100vh;
}

.main-content {
  margin-left: var(--sidebar-width);
  padding: 24px;
}
</style>
