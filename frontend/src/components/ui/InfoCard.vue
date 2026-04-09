<script setup lang="ts">
import { computed, useSlots } from 'vue'
import Card from './Card.vue'

interface InfoCardProps {
  title: string
  icon: any
  iconBackgroundColor: string
  iconTransform?: string
  iconColor?: string
  addToHeader?: boolean
}

const props = withDefaults(defineProps<InfoCardProps>(), {
  iconColor: 'var(--neutral)',
  addToHeader: false,
  iconTransform: 'none',
})

const iconStyle = computed(() => ({
  color: props.iconColor,
  transform: props.iconTransform,
}))

const slots = useSlots()
const shouldRenderExtraHeaderContent = props.addToHeader || Boolean(slots['extra-header-content'])
</script>

<template>
  <Card class="card">
    <template #card-header>
      <div class="header">
        <div class="card-icon" :style="{ backgroundColor: props.iconBackgroundColor }">
          <component :is="props.icon" size="24" aria-hidden="true" :style="iconStyle" />
        </div>
        <span class="title">{{ props.title }}</span>
        <template v-if="shouldRenderExtraHeaderContent">
          <slot name="extra-header-content" />
        </template>
      </div>
    </template>

    <template #card-content>
      <slot class="card-content" />
    </template>
  </Card>
</template>

<style scoped>
.header {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  gap: 10px;
}

.card {
  margin: 0;
}

.card-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  border-radius: 9px;
}

.title {
  display: inline-flex;
  align-items: center;
  font-weight: 1000;
  white-space: nowrap;
  text-align: left;
}

.description {
  margin: 0;
}

.card-content {
  align-items: center;
}
</style>
