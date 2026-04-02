<script setup lang="ts">
import { Activity, BookText, Clock3, ShieldCheck } from '@lucide/vue'

type LogLevel = 'Info' | 'Warning' | 'Resolved'

interface LogEntry {
  id: number
  title: string
  detail: string
  actor: string
  time: string
  level: LogLevel
}

const entries: LogEntry[] = [
  {
    id: 1,
    title: 'Temperature deviation acknowledged',
    detail: 'Cool room 2 exceeded target range for 12 minutes and was assigned for follow-up.',
    actor: 'System',
    time: '07:42',
    level: 'Warning',
  },
  {
    id: 2,
    title: 'Opening checklist completed',
    detail: 'Kitchen team closed all morning tasks and added a note about prep station cleaning.',
    actor: 'Nora',
    time: '08:14',
    level: 'Resolved',
  },
  {
    id: 3,
    title: 'User access updated',
    detail: 'Shift lead permissions were adjusted for the evening supervisor group.',
    actor: 'Liam',
    time: '09:05',
    level: 'Info',
  },
]

function levelClass(level: LogLevel) {
  return `level-pill level-${level.toLowerCase()}`
}
</script>

<template>
  <div class="logs-view">
    <h1>Logs</h1>

    <section class="overview-grid">
      <article class="feature-card">
        <p class="section-label">Activity Feed</p>
        <h2>Keep system events, operator actions, and notes in one timeline.</h2>
        <p>
          This page is intentionally simple: a compact summary row on top and a readable event list
          underneath.
        </p>
      </article>

      <article class="metric-card">
        <Activity :size="18" aria-hidden="true" />
        <strong>24</strong>
        <span>Events today</span>
      </article>

      <article class="metric-card">
        <ShieldCheck :size="18" aria-hidden="true" />
        <strong>3</strong>
        <span>Items resolved</span>
      </article>
    </section>

    <section class="content-grid">
      <article class="timeline-panel">
        <div class="panel-heading">
          <div>
            <p class="section-label">Recent</p>
            <h3>Latest entries</h3>
          </div>
          <span class="subtle">Newest first</span>
        </div>

        <ul class="timeline">
          <li v-for="entry in entries" :key="entry.id" class="timeline-entry">
            <div class="timeline-dot" aria-hidden="true" />

            <div class="timeline-body">
              <div class="timeline-topline">
                <strong>{{ entry.title }}</strong>
                <span :class="levelClass(entry.level)">{{ entry.level }}</span>
              </div>

              <p>{{ entry.detail }}</p>

              <div class="meta">
                <span>{{ entry.actor }}</span>
                <span>{{ entry.time }}</span>
              </div>
            </div>
          </li>
        </ul>
      </article>

      <article class="notes-panel">
        <div class="mini-card">
          <BookText :size="18" aria-hidden="true" />
          <div>
            <strong>Easy to extend</strong>
            <p>Swap `entries` for backend records and keep the rest of the template intact.</p>
          </div>
        </div>

        <div class="mini-card">
          <Clock3 :size="18" aria-hidden="true" />
          <div>
            <strong>Good next step</strong>
            <p>Add filters for module, severity, and date range above the timeline.</p>
          </div>
        </div>
      </article>
    </section>
  </div>
</template>

<style scoped>
.logs-view {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.overview-grid,
.content-grid {
  display: grid;
  gap: 16px;
}

.overview-grid {
  grid-template-columns: 2fr 1fr 1fr;
}

.content-grid {
  grid-template-columns: 2fr 1fr;
}

.feature-card,
.metric-card,
.timeline-panel,
.notes-panel {
  border-radius: 24px;
  border: 1px solid rgba(15, 23, 42, 0.08);
}

.feature-card,
.timeline-panel,
.notes-panel {
  padding: 24px;
}

.feature-card {
  background: linear-gradient(135deg, #eef3f0 0%, #dde7e1 100%);
}

.feature-card h2,
.timeline-panel h3 {
  margin: 0 0 10px;
}

.feature-card p:last-child,
.timeline-body p,
.mini-card p {
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

.metric-card {
  padding: 22px;
  background: #f7f7f6;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.metric-card strong {
  font-size: 30px;
}

.metric-card span,
.subtle,
.meta {
  color: var(--text-secondary);
}

.panel-heading,
.timeline-topline,
.meta {
  display: flex;
  justify-content: space-between;
  gap: 12px;
}

.timeline {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.timeline-entry {
  display: grid;
  grid-template-columns: 16px 1fr;
  gap: 14px;
  align-items: start;
}

.timeline-dot {
  width: 10px;
  height: 10px;
  margin-top: 8px;
  border-radius: 50%;
  background: #334155;
}

.timeline-body {
  padding: 16px 18px;
  border-radius: 18px;
  background: #f7f7f6;
}

.timeline-body p {
  margin: 10px 0 14px;
}

.notes-panel {
  display: flex;
  flex-direction: column;
  gap: 16px;
  background: #fbfbfa;
}

.mini-card {
  display: flex;
  gap: 14px;
  padding: 16px;
  border-radius: 18px;
  background: #ffffff;
}

.mini-card strong {
  display: block;
  margin-bottom: 6px;
}

.level-pill {
  padding: 7px 11px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 700;
}

.level-info {
  background: #e0f2fe;
  color: #0369a1;
}

.level-warning {
  background: #fef3c7;
  color: #b45309;
}

.level-resolved {
  background: #dcfce7;
  color: #15803d;
}

@media (max-width: 960px) {
  .overview-grid,
  .content-grid {
    grid-template-columns: 1fr;
  }
}
</style>
