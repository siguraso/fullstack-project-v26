<script setup lang="ts">
import { Bell, Building2, ShieldCheck, SlidersHorizontal } from '@lucide/vue'
import ViewHeader from '@/components/ui/ViewHeader.vue'

const preferenceGroups = [
  {
    title: 'Workspace defaults',
    description: 'Store identity, reporting timezone, and default compliance module settings.',
    icon: Building2,
    action: 'Edit workspace',
  },
  {
    title: 'Notifications',
    description: 'Alert routing for critical deviations, checklist misses, and inspection reminders.',
    icon: Bell,
    action: 'Manage alerts',
  },
  {
    title: 'Access controls',
    description: 'Permission boundaries, approval flows, and sign-in guardrails for staff accounts.',
    icon: ShieldCheck,
    action: 'Review access',
  },
]
</script>

<template>
  <div class="settings-view">
    <ViewHeader title="Settings" :options="[]" :routes="[]" />

    <section class="hero-card">
      <div>
        <p class="section-label">Configuration</p>
        <h2>Control how the compliance workspace behaves day to day.</h2>
        <p>
          This page can act as the home for operational settings while the rest of the app focuses
          on execution and reporting.
        </p>
      </div>
      <div class="hero-icon">
        <SlidersHorizontal :size="28" aria-hidden="true" />
      </div>
    </section>

    <section class="settings-grid">
      <article v-for="group in preferenceGroups" :key="group.title" class="settings-card">
        <div class="card-top">
          <component :is="group.icon" :size="18" aria-hidden="true" />
          <span>{{ group.action }}</span>
        </div>
        <h3>{{ group.title }}</h3>
        <p>{{ group.description }}</p>
        <button type="button">{{ group.action }}</button>
      </article>
    </section>
  </div>
</template>

<style scoped>
.settings-view {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.hero-card,
.settings-card {
  border: 1px solid rgba(15, 23, 42, 0.08);
  border-radius: 24px;
}

.hero-card {
  padding: 28px;
  background: linear-gradient(135deg, #eef0e7 0%, #dde5d5 100%);
  display: flex;
  justify-content: space-between;
  gap: 20px;
  align-items: flex-start;
}

.hero-card h2,
.settings-card h3 {
  margin: 0 0 10px;
}

.hero-card p:last-child,
.settings-card p {
  margin: 0;
  color: var(--text-secondary);
}

.section-label {
  margin: 0 0 8px;
  font-size: 12px;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: var(--text-secondary);
}

.hero-icon {
  width: 60px;
  height: 60px;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.72);
  display: grid;
  place-items: center;
  flex-shrink: 0;
}

.settings-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
}

.settings-card {
  padding: 24px;
  background: #f8f8f6;
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.card-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: var(--text-secondary);
  font-size: 13px;
}

.settings-card button {
  align-self: flex-start;
  min-height: 40px;
  padding: 0 14px;
  border-radius: 999px;
  border: 1px solid var(--border);
  background: #ffffff;
  color: var(--text);
  font-weight: 600;
}

@media (max-width: 960px) {
  .settings-grid {
    grid-template-columns: 1fr;
  }

  .hero-card {
    flex-direction: column;
  }
}
</style>
