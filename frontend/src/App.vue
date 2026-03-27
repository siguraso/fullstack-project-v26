<script lang="ts" setup>
import Sidebar from './components/Sidebar.vue';
import Dashboard from './views/dashboard/Dashboard.vue';

</script>

<template>
  <div class="app-shell">
    <Sidebar />
    <main class="main-content">
      <Dashboard />
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