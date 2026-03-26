<script lang="ts" setup>
import Card from '@/components/ui/Card.vue';
import PieChart from './components/PieChart.vue';

</script>

<template>
    <h1>Dashboard</h1>
    <div class="dashboard-grid">
      <Card>
      <template #card-header>
        Task Completion
      </template>

      <template #card-content>
        <PieChart :greenPiece="3432" :orangePiece="7540" :greenLabel="'completed'" :orangeLabel="'not completed'" />
      </template>
    </Card>

    <Card>
      <template #card-header>
        Task Completion
      </template>

      <template #card-content>
        <PieChart :greenPiece="3432" :orangePiece="7540" :greenLabel="'completed'" :orangeLabel="'not completed'" />
      </template>
    </Card>

    <Card>
      <template #card-header>
        Task Completion
      </template>

      <template #card-content>
        <PieChart :greenPiece="3432" :orangePiece="7540" :greenLabel="'completed'" :orangeLabel="'not completed'" />
      </template>
    </Card>
  </div>
</template>

<style scoped>
.dashboard-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 20px;
}

.dashboard-grid > :nth-child(3) {
  grid-column: 1 / -1;
}
</style>