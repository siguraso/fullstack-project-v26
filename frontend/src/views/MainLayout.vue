<script setup lang="ts">
import Sidebar from '@/components/Sidebar.vue'
</script>

<template>
  <div class="dashboard-shell">
    <Sidebar />

    <main class="dashboard-main">
      <router-view />
    </main>
  </div>
</template>

<style scoped>
.dashboard-shell {
  --sidebar-width: max(15vw, 220px);
  min-height: 100vh;
}

.dashboard-main {
  margin-left: var(--sidebar-width);
  padding: 24px;
}
</style>
