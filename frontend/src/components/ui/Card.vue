<script setup lang="ts"></script>

<template>
  <article class="card">
    <header class="card-header">
      <slot name="cardHeader" />
    </header>

    <section class="card-content">
      <slot name="cardContent" />
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
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 12px;
}
</style>