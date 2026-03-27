<script setup lang="ts"></script>

<template>
  <article class="card">
    <header class="card-header">
      <slot name="card-header" />
    </header>

    <section class="card-content">
      <slot name="card-content" />
    </section>
  </article>
</template>

<style scoped>
.card {
  background-color: var(--bg);
  border: 1px solid var(--stroke);
  border-radius: 8px;
  padding: 16px;
}

.card-header {
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 20px;
  align-self: center;
}
</style>