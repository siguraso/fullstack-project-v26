<script setup lang="ts">
import InfoCard from '@/components/ui/InfoCard.vue'
import { TriangleAlert } from '@lucide/vue'
</script>

<template>
  <InfoCard
    class="guide-card"
    title="Deviation guide"
    :icon="TriangleAlert"
    iconBackgroundColor="var(--icon-bg-yellow)"
    iconColor="var(--icon-stroke-yellow)"
  >
    <p class="intro-copy">
      The form now follows the PDF structure. Capture each step in order so the incident, cause,
      and follow-up stay together in one record.
    </p>

    <div class="hint-list">
      <article class="hint-card">
        <h3>Sections to complete</h3>
        <p><strong>Header:</strong> Title, reference number, severity, and who is involved.</p>
        <p><strong>Middle:</strong> What happened, immediate action, cause, and prevention.</p>
        <p><strong>Close-out:</strong> Completion date and follow-up when the action is done.</p>
      </article>

      <article class="hint-card">
        <h3>Severity quick rule</h3>
        <p><strong>Minor:</strong> Limited deviation with low operational impact.</p>
        <p><strong>Major:</strong> Significant issue that needs managed follow-up.</p>
        <p><strong>Critical:</strong> Immediate safety or compliance risk.</p>
      </article>
    </div>
  </InfoCard>
</template>

<style scoped>
.guide-card {
  margin: 0;
  height: 100%;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.intro-copy {
  margin: 0;
  color: var(--text-secondary);
  line-height: 1.6;
  font-size: 14px;
}

.hint-list {
  margin-top: 4px;
  flex: 1;
  display: grid;
  grid-template-rows: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.hint-card {
  border: 1px solid var(--border);
  border-left: 4px solid var(--neutral);
  border-radius: 10px;
  background: #ffffff;
  padding: 14px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 8px;
}

.hint-card h3 {
  margin: 0;
  font-size: 15px;
  color: var(--text);
}

.hint-card p {
  margin: 0;
  color: var(--text-secondary);
  font-size: 13.5px;
  line-height: 1.45;
}

@media (max-width: 1250px) {
  .guide-card {
    padding: 18px;
  }

  .hint-list {
    grid-template-rows: auto;
  }
}
</style>
