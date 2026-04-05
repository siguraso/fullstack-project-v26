<script setup lang="ts">
import Card from '@/components/ui/Card.vue'
import type { TemperatureZone } from '@/types/temperature-zone'
import { ref } from 'vue'

const props = defineProps<{
  zone: TemperatureZone
}>()

const emit = defineEmits<{
  (event: 'close'): void
  (event: 'save', updatedZone: TemperatureZone): void
  (event: 'delete', zoneId: number): void
}>()

function closeOverlay() {
  emit('close')
}

function saveChanges() {
  const updatedZone: TemperatureZone = {
    id: props.zone.id,
    name: name.value,
    lowerLimitCelsius: lowerLimit.value,
    upperLimitCelsius: upperLimit.value,
    active: props.zone.active,
  }
  emit('save', updatedZone)
}

function deleteZone() {
  emit('delete', props.zone.id)
}

const name = ref(props.zone.name)
const lowerLimit = ref(props.zone.lowerLimitCelsius)
const upperLimit = ref(props.zone.upperLimitCelsius)
</script>

<template>
  <Card class="card">
    <template #card-header>
      <div class="header-row">
        <h3 class="title">Editing Temperature Zone: {{ zone.name }}</h3>
        <button class="delete-button" @click="deleteZone">Remove Zone</button>
      </div>
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
        <button class="save-btn" @click="saveChanges">Save Changes</button>
      </div>
    </template>
  </Card>
</template>

<style scoped>
.card {
  width: 50%;
  max-width: 480px;
  max-height: calc(100vh - 3rem);
  overflow: auto;
}

.title {
  margin: 0;
}

.header-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 0.7rem;
}
.close-button {
  background-color: var(--bg);
  color: var(--text-primary);
  margin-top: 1.5rem;
}

.close-button:hover {
  background-color: var(--bg-hover);
}

.close-button:active {
  background-color: var(--bg-active);
}

.subtext {
  margin: 10px 0px 5px 0px;
  color: var(--text-secondary);
  font-size: 12px;
}

.delete-button {
  background-color: var(--cta-red-btn);
  color: white;
  width: 150px;
}

.edit-input {
  width: 100%;
}

.save-btn {
  margin-top: 1.5rem;
  width: 100%;
}

.buttons {
  display: grid;
  grid-template-columns: 1fr 2fr;
  gap: 10px;
}
</style>
