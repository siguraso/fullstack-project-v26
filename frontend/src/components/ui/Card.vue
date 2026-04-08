<script setup lang="ts">
const props = withDefaults(
  defineProps<{
    headerDisabled?: boolean
  }>(),
  {
    headerDisabled: false,
  },
)
</script>

<template>
  <article class="card">
    <header v-if="!props.headerDisabled" class="card-header">
      <slot name="card-header" />
    </header>

    <section class="card-content">
      <slot name="card-content" />
    </section>
  </article>
</template>

<style scoped>
.card {
  background-color: var(--bg-secondary);
  border-radius: 10px;
  padding: 16px;
  box-shadow: var(--shadow-soft);
}

.card-header {
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 20px;
  text-align: left;
}
</style>
