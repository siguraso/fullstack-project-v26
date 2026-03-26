<script setup lang="ts">
import Sidebar from './components/Sidebar.vue';
import PieChart from './views/dashboard/components/PieChart.vue';

</script>

<template>
  <div class="main-content">
    <Sidebar />
    <PieChart :greenPiece="3432" :orangePiece="7540" :greenLabel="'completed'" :orangeLabel="'not completed'" />
  </div>
</template>

<style scoped>
.main-content {
  display: flex;
  margin-left: 15%;
}
</style>