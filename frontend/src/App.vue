<script lang="ts" setup>
import Sidebar from './components/Sidebar.vue'
import DashboardView from './views/dashboard/DashboardView.vue'
</script>

<template>
  <div class="app-shell">
    <main class="main-content">
      <router-view />
    </main>
  </div>
</template>

<style scoped></style>
