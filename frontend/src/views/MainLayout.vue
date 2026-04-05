<script setup lang="ts">
import NavBar from '@/components/NavBar.vue'
import Sidebar from '@/components/Sidebar.vue'
</script>

<template>
  <div class="screen-overlay">
    <NavBar />
    <div class="dashboard-shell">
      <Sidebar />

      <main class="dashboard-main">
        <router-view />
      </main>
    </div>
  </div>
</template>

<style scoped>
.dashboard-shell {
  --navbar-height: 54px;
  --sidebar-width: max(15vw, 220px);
  min-height: 100vh;
  padding-top: var(--navbar-height);
}

.dashboard-main {
  margin-left: var(--sidebar-width);
  padding: 24px;
}
</style>
