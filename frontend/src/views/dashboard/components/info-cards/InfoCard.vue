<script setup lang="ts">
import Card from '@/components/ui/Card.vue'

interface InfoCardProps {
  title: string
  icon: any
  iconColor: string
}

const props = defineProps<InfoCardProps>()
</script>

<template>
  <Card class="card">
    <template #card-header>
      <div class="header">
        <div class="card-icon" :style="{ backgroundColor: props.iconColor }">
          <component :is="props.icon" size="24" aria-hidden="true" />
        </div>
        <span class="title">{{ props.title }}</span>
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
  font-weight: 1000;
}

.description {
  margin: 0;
}

.card-content {
  align-items: center;
}
</style>
