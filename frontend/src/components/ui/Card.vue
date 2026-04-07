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
  box-shadow:
    0 2px 6px rgba(15, 23, 42, 0.06),
    0 10px 24px rgba(15, 23, 42, 0.08);
}

.card-header {
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 20px;
  text-align: left;
}
</style>
