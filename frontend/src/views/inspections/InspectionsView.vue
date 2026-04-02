<script setup lang="ts">
import { CalendarClock, ClipboardCheck, ScanSearch, Shield } from '@lucide/vue'

type InspectionStatus = 'Ready' | 'Scheduled' | 'Follow-up'

interface InspectionItem {
  id: number
  title: string
  area: string
  owner: string
  date: string
  status: InspectionStatus
}

const inspections: InspectionItem[] = [
  {
    id: 1,
    title: 'Weekly hygiene walkthrough',
    area: 'Kitchen',
    owner: 'Store lead',
    date: 'Tomorrow 09:00',
    status: 'Ready',
  },
  {
    id: 2,
    title: 'Supplier traceability review',
    area: 'Storage',
    owner: 'Operations',
    date: 'Friday 11:30',
    status: 'Scheduled',
  },
  {
    id: 3,
    title: 'Fire safety follow-up',
    area: 'Back office',
    owner: 'Facility team',
    date: 'Next week',
    status: 'Follow-up',
  },
]

function statusClass(status: InspectionStatus) {
  return `inspection-pill inspection-${status.toLowerCase().replace('-', '')}`
}
</script>

<template>
  <div class="inspections-view">
    <h1>Inspections</h1>

    <section class="hero-grid">
      <article class="lead-card">
        <p class="section-label">Inspection Hub</p>
        <h2>Prepare upcoming checks before this turns into a larger workflow.</h2>
        <p>
          The layout is built so you can later plug in scheduled inspections, owners, documents, and
          readiness states without redesigning the whole page.
        </p>
      </article>

      <article class="focus-card">
        <CalendarClock :size="18" aria-hidden="true" />
        <strong>Tomorrow 09:00</strong>
        <span>Next planned inspection</span>
      </article>
    </section>

    <section class="main-grid">
      <article class="schedule-panel">
        <div class="panel-heading">
          <div>
            <p class="section-label">Schedule</p>
            <h3>Upcoming inspections</h3>
          </div>
          <span class="subtle">{{ inspections.length }} planned items</span>
        </div>

        <div class="inspection-table">
          <div v-for="inspection in inspections" :key="inspection.id" class="inspection-row">
            <div>
              <strong>{{ inspection.title }}</strong>
              <p>{{ inspection.area }} • {{ inspection.owner }}</p>
            </div>

            <div class="inspection-meta">
              <span>{{ inspection.date }}</span>
              <span :class="statusClass(inspection.status)">{{ inspection.status }}</span>
            </div>
          </div>
        </div>
      </article>

      <article class="prep-panel">
        <div class="prep-card">
          <ClipboardCheck :size="18" aria-hidden="true" />
          <div>
            <strong>Readiness checklist</strong>
            <p>Add document links, assigned staff, and pre-check tasks here.</p>
          </div>
        </div>

        <div class="prep-card">
          <ScanSearch :size="18" aria-hidden="true" />
          <div>
            <strong>Evidence and notes</strong>
            <p>This column is a good place for uploads, comments, or recent findings.</p>
          </div>
        </div>

        <div class="prep-card">
          <Shield :size="18" aria-hidden="true" />
          <div>
            <strong>Compliance status</strong>
            <p>Use this card for pass rate, open issues, or follow-up ownership later on.</p>
          </div>
        </div>
      </article>
    </section>
  </div>
</template>

<style scoped>
.inspections-view {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.hero-grid,
.main-grid {
  display: grid;
  gap: 16px;
}

.hero-grid {
  grid-template-columns: 2fr 1fr;
}

.main-grid {
  grid-template-columns: 2fr 1fr;
}

.lead-card,
.focus-card,
.schedule-panel,
.prep-panel {
  border-radius: 24px;
  border: 1px solid rgba(15, 23, 42, 0.08);
}

.lead-card,
.schedule-panel,
.prep-panel {
  padding: 24px;
}

.lead-card {
  background: linear-gradient(135deg, #f1f0fb 0%, #e2e7f5 100%);
}

.focus-card {
  padding: 24px;
  background: #f7f7f6;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 10px;
}

.focus-card strong {
  font-size: 28px;
}

.section-label {
  margin: 0 0 8px;
  font-size: 12px;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: var(--text-secondary);
}

.lead-card h2,
.schedule-panel h3 {
  margin: 0 0 10px;
}

.lead-card p:last-child,
.subtle,
.inspection-row p,
.prep-card p,
.focus-card span {
  margin: 0;
  color: var(--text-secondary);
}

.panel-heading,
.inspection-meta {
  display: flex;
  justify-content: space-between;
  gap: 12px;
}

.inspection-table {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.inspection-row {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: center;
  padding: 16px 18px;
  border-radius: 18px;
  background: #f7f7f6;
}

.inspection-row strong {
  display: block;
  margin-bottom: 4px;
}

.inspection-meta {
  align-items: center;
  flex-wrap: wrap;
}

.prep-panel {
  display: flex;
  flex-direction: column;
  gap: 16px;
  background: #fbfbfa;
}

.prep-card {
  display: flex;
  gap: 14px;
  padding: 16px;
  border-radius: 18px;
  background: #ffffff;
}

.prep-card strong {
  display: block;
  margin-bottom: 6px;
}

.inspection-pill {
  padding: 7px 11px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 700;
}

.inspection-ready {
  background: #dcfce7;
  color: #15803d;
}

.inspection-scheduled {
  background: #e0f2fe;
  color: #0369a1;
}

.inspection-followup {
  background: #fef3c7;
  color: #b45309;
}

@media (max-width: 960px) {
  .hero-grid,
  .main-grid,
  .inspection-row {
    grid-template-columns: 1fr;
    display: grid;
  }

  .inspection-meta {
    justify-content: start;
  }
}
</style>
