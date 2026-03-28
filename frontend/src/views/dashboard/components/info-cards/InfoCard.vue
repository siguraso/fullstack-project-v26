<script setup lang="ts">
import Card from '@/components/ui/Card.vue'

interface InfoCardProps {
  title: string
  icon: any
  cardColor: string
}

const props = defineProps<InfoCardProps>()
</script>

<template>
  <Card>
    <template #card-header>
      <div class="header" :style="{ color: props.cardColor }">
        <component :is="props.icon" size="20" aria-hidden="true" />
        <span class="title">{{ props.title }}</span>
      </div>
    </template>

    <template #card-content>
      <slot />
    </template>
  </Card>
</template>

<style scoped>
.header {
  display: flex;
  align-items: center;
  gap: 10px;
}

.title {
  font-weight: bold;
}

.description {
  margin: 0;
}
</style>
