<script setup lang="ts">
import {
  Thermometer,
  BrushCleaning,
  Martini,
  FileText,
  Ellipsis,
  type LucideIcon,
} from '@lucide/vue'
import type { Deviation } from '@/stores/deviation'

defineProps<{ active: Deviation['category'] }>()
defineEmits<{
  select: [value: Deviation['category']]
}>()

const items: Array<{ value: Deviation['category']; label: string; icon: LucideIcon }> = [
  { value: 'TEMPERATURE', label: 'Temperature deviation', icon: Thermometer },
  { value: 'HYGIENE', label: 'Hygiene issue', icon: BrushCleaning },
  { value: 'ALCOHOL', label: 'Alcohol incident', icon: Martini },
  { value: 'DOCUMENTATION', label: 'Documentation missing', icon: FileText },
  { value: 'OTHER', label: 'Other', icon: Ellipsis },
]
</script>

<template>
  <div class="quick-actions">
    <button
        v-for="item in items"
        :key="item.value"
        type="button"
        class="quick-action"
        :class="{ active: active === item.value }"
        @click="$emit('select', item.value)"
    >
      <component :is="item.icon" :size="20" class="icon" aria-hidden="true" />
      <span class="label">{{ item.label }}</span>
    </button>
  </div>
</template>

<style scoped>
.quick-actions {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));
  gap: 12px;
  margin-bottom: 18px;
}

.quick-action {
  min-height: 78px;
  padding: 14px 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 10px;
  text-transform: uppercase;
  letter-spacing: 0.04em;
  font-size: 10px;
  font-weight: 700;
  color: var(--text-secondary);
  border: 1px solid var(--border);
  border-radius: 10px;
  background: var(--surface-muted);
}

.quick-action:hover {
  background: var(--surface);
}

.quick-action.active {
  border: 2px solid var(--cta-red);
  box-shadow: var(--shadow-soft);
  color: var(--text);
}

.icon {
  color: var(--text-muted);
}

.quick-action.active .icon {
  color: var(--cta-red);
}

.label {
  text-align: center;
  line-height: 1.3;
}
</style>