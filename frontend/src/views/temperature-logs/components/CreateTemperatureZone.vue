<script setup lang="ts">
import Card from '@/components/ui/Card.vue'
import type { TemperatureZone } from '@/types/temperature-zone'
import { ref } from 'vue'

const emit = defineEmits<{
  (event: 'close'): void
  (event: 'create', payload: Omit<TemperatureZone, 'id'>): void
}>()

const name = ref('')
const lowerLimit = ref(0)
const upperLimit = ref(5)

function closeOverlay() {
  emit('close')
}

function createZone() {
  emit('create', {
    name: name.value,
    lowerLimitCelsius: lowerLimit.value,
    upperLimitCelsius: upperLimit.value,
    active: true,
  })
}
</script>

<template>
  <Card class="card">
    <template #card-header>
      <h3 class="title">Create new temperature zone</h3>
    </template>

    <template #card-content>
      <p class="subtext">Temperature Zone Name</p>
      <input type="text" v-model="name" class="edit-input" />
      <p class="subtext">Lower Limit (°C)</p>
      <input type="number" v-model.number="lowerLimit" class="edit-input" />
      <p class="subtext">Upper Limit (°C)</p>
      <input type="number" v-model.number="upperLimit" class="edit-input" />
      <div class="buttons">
        <button class="close-button" @click="closeOverlay">Cancel</button>
        <button class="save-btn" @click="createZone">Create Zone</button>
      </div>
    </template>
  </Card>
</template>

<style scoped>
.title {
  margin: 0;
}

.card {
  width: 50%;
  max-width: 480px;
  max-height: calc(100vh - 3rem);
  overflow: auto;
}

.subtext {
  margin: 10px 0px 5px 0px;
  color: var(--text-secondary);
  font-size: 12px;
}

.edit-input {
  width: 100%;
}

.save-btn {
  margin-top: 1.5rem;
  width: 100%;
}

.close-button {
  background-color: var(--bg);
  color: var(--text-primary);
  margin-top: 1.5rem;
}

.buttons {
  display: grid;
  grid-template-columns: 1fr 2fr;
  gap: 10px;
}
</style>
