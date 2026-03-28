<script setup lang="ts">
import { ref } from 'vue'

const props = withDefaults(
  defineProps<{
    title?: string
    options: string[]
    routes: string[]
  }>(),
  {
    title: '',
  },
)

const activeIndex = ref(0)

function setActive(index: number) {
  activeIndex.value = index
}
</script>

<template>
  <div class="view-header">
    <h1 class="title">
      {{ props.title }}
    </h1>
    <div class="actions">
      <button
        v-for="(option, index) in props.options"
        :key="option"
        :class="index === activeIndex ? 'menu-button-active' : 'menu-button-inactive'"
        @click="setActive(index)"
      >
        {{ option }}
      </button>
    </div>
    <div class="spacer" aria-hidden="true" />
  </div>
</template>

<style scoped>
.view-header {
  --action-button-bg: #d9d9d9;
  --action-button-hover-bg: #b3b3b3;

  display: grid;
  grid-template-columns: 1fr auto 1fr;
  align-items: center;
  column-gap: 12px;
  margin-bottom: 40px;
}

.title {
  margin: 0;
  justify-self: start;
}

.actions {
  display: flex;
  grid-column: 2;
  justify-content: center;
  align-items: center;
  gap: 10px;
  padding: 5px;
  border-radius: 70px;
  border: 2px solid var(--stroke);
}

.actions .menu-button-inactive {
  background-color: var(--bg);
  color: var(--text-secondary);
  border: none;
  border-radius: 50px;
  padding: 6px 12px;
  cursor: pointer;
  font-weight: bold;
  transition:
    background-color 220ms ease,
    color 220ms ease;
}

.actions .menu-button-inactive:hover {
  background-color: var(--stroke);
}

.actions .menu-button-active {
  color: white;
  background-color: var(--neutral);
  border: none;
  border-radius: 50px;
  padding: 6px 12px;
  cursor: pointer;
  font-weight: bold;
}

.spacer {
  justify-self: end;
}
</style>
