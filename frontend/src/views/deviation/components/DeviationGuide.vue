<script setup lang="ts">
import { TriangleAlert } from '@lucide/vue'
</script>

<template>
  <aside class="guide-card">
    <div class="panel-head">
      <div class="panel-icon">
        <TriangleAlert :size="20" aria-hidden="true" />
      </div>
      <h2 class="panel-title">Deviation guide</h2>
    </div>

    <p class="intro-copy">
      Use specific titles and short factual descriptions. The faster a teammate understands impact
      and urgency, the faster the issue gets resolved.
    </p>

    <div class="hint-list">
      <article class="hint-card">
        <h3>Recommended by category</h3>
        <p><strong>Temperature:</strong> Include location, measured value, and threshold.</p>
        <p><strong>Hygiene:</strong> Mention area, contamination risk, and immediate action.</p>
        <p><strong>Documentation:</strong> Reference missing or outdated record.</p>
      </article>

      <article class="hint-card">
        <h3>Severity quick rule</h3>
        <p><strong>Low/Medium:</strong> Minor process issue with low risk.</p>
        <p><strong>High/Critical:</strong> Safety or compliance risk requiring immediate follow-up.</p>
      </article>
    </div>
  </aside>
</template>

<style scoped>
.guide-card {
  background: linear-gradient(180deg, var(--surface), #f8f9fb);
  border: 1px solid var(--border);
  border-radius: 14px;
  box-shadow: var(--shadow-soft);
  padding: 22px;
  height: 100%;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.panel-head {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.panel-title {
  margin: 0;
  font-size: 24px;
  line-height: 1.15;
  color: var(--text);
  font-weight: 800;
}

.panel-icon {
  width: 36px;
  height: 36px;
  border-radius: 9px;
  background: var(--neutral);
  color: #ffffff;
  display: grid;
  place-items: center;
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



