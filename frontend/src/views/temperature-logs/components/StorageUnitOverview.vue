<script setup lang="ts">
import InfoCard from '@/components/ui/InfoCard.vue'
import { Box } from '@lucide/vue'

const units = [
  { id: 1, name: 'Fridge1' },
  { id: 2, name: 'Freezer room' },
  { id: 3, name: 'Room' },
]
</script>

<template>
  <InfoCard
    class="info-card"
    title="Storage Unit Overview"
    :icon="Box"
    iconBackgroundColor="var(--neutral)"
    iconColor="white"
  >
    <div class="unit-list">
      <div v-for="unit in units" :key="unit.id" class="unit-item">
        <Box class="unit-icon" />
        <span>{{ unit.name }}</span>
      </div>
    </div>
  </InfoCard>
</template>

<style scoped></style>
