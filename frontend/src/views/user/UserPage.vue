<script setup lang="ts">
import { computed } from 'vue'
import ViewHeader from '@/components/ui/ViewHeader.vue'
import { BadgeCheck, Clock3, KeyRound, Mail, MapPin, ShieldCheck, UserRound } from '@lucide/vue'

type OrgUser = {
  initials: string
  name: string
  email: string
  role: string
  team: string
  location: string
  status: 'Active' | 'Pending' | 'Restricted'
  lastActive: string
  permissions: string[]
}

const organization = {
  name: 'Regula Hospitality Group',
  orgNumber: 'RG-24-118',
  headquarters: 'Bristol, United Kingdom',
  coverage: '8 active sites',
}

const policies = [
  {
    label: 'User invitations',
    description: 'Managers can add staff using predefined role templates.',
    state: 'Enabled',
  },
  {
    label: 'Permission review cycle',
    description: 'Access scope is reviewed at the end of each trading month.',
    state: 'Monthly',
  },
  {
    label: 'Restricted account handling',
    description: 'Flagged users keep read-only visibility until admin review.',
    state: 'Protected',
  },
]

const users: OrgUser[] = [
  {
    initials: 'AT',
    name: 'Avery Thompson',
    email: 'avery.thompson@regula.co',
    role: 'Store Operations Manager',
    team: 'Operations',
    location: 'Bristol',
    status: 'Active',
    lastActive: 'Today, 08:24',
    permissions: ['Dashboard access', 'Checklist publishing', 'Incident review', 'Team assignments'],
  },
  {
    initials: 'JM',
    name: 'Jordan Mitchell',
    email: 'jordan.mitchell@regula.co',
    role: 'Compliance Lead',
    team: 'Food Safety',
    location: 'Manchester',
    status: 'Active',
    lastActive: 'Today, 07:55',
    permissions: ['Audit exports', 'Policy updates', 'Incident review', 'Checklist approvals'],
  },
  {
    initials: 'RK',
    name: 'Riley Khan',
    email: 'riley.khan@regula.co',
    role: 'Area Supervisor',
    team: 'Regional Ops',
    location: 'Leeds',
    status: 'Active',
    lastActive: 'Yesterday',
    permissions: ['Dashboard access', 'Store oversight', 'Team assignments', 'Checklist approvals'],
  },
  {
    initials: 'SP',
    name: 'Sophie Patel',
    email: 'sophie.patel@regula.co',
    role: 'Training Coordinator',
    team: 'People & Training',
    location: 'Liverpool',
    status: 'Pending',
    lastActive: 'Invitation sent',
    permissions: ['Training records', 'Checklist visibility', 'Team onboarding'],
  },
  {
    initials: 'DN',
    name: 'Daniel Novak',
    email: 'daniel.novak@regula.co',
    role: 'Incident Reviewer',
    team: 'Risk',
    location: 'Birmingham',
    status: 'Restricted',
    lastActive: '2 days ago',
    permissions: ['Incident review', 'Read-only reporting'],
  },
]

const summaryCards = computed(() => {
  const activeUsers = users.filter((user) => user.status === 'Active').length
  const uniqueRoles = new Set(users.map((user) => user.role)).size
  const elevatedAccess = users.filter((user) =>
    user.permissions.some((permission) =>
      ['Audit exports', 'Policy updates', 'Store oversight', 'Team assignments'].includes(
        permission,
      ),
    ),
  ).length

  return [
    {
      label: 'Configured users',
      value: String(users.length),
      description: 'Accounts listed in the current organisation setup.',
      icon: UserRound,
      accent: 'var(--primary)',
    },
    {
      label: 'Active access',
      value: String(activeUsers),
      description: 'Users with current sign-in and operational access.',
      icon: BadgeCheck,
      accent: 'var(--secondary)',
    },
    {
      label: 'Roles in use',
      value: String(uniqueRoles),
      description: 'Distinct responsibility levels applied across the team.',
      icon: ShieldCheck,
      accent: 'var(--tertiary)',
    },
    {
      label: 'Elevated access',
      value: String(elevatedAccess),
      description: 'Accounts with higher-impact permissions assigned.',
      icon: KeyRound,
      accent: 'var(--neutral)',
    },
  ]
})

const roleBreakdown = computed(() => {
  const counts = new Map<string, number>()

  for (const user of users) {
    counts.set(user.role, (counts.get(user.role) ?? 0) + 1)
  }

  return Array.from(counts.entries()).map(([role, count]) => ({ role, count }))
})

const permissionCoverage = computed(() => {
  const counts = new Map<string, number>()

  for (const user of users) {
    for (const permission of user.permissions) {
      counts.set(permission, (counts.get(permission) ?? 0) + 1)
    }
  }

  return Array.from(counts.entries())
    .map(([permission, count]) => ({ permission, count }))
    .sort((left, right) => right.count - left.count)
    .slice(0, 6)
})

function statusClass(status: OrgUser['status']) {
  if (status === 'Active') {
    return 'status-pill status-active'
  }

  if (status === 'Pending') {
    return 'status-pill status-pending'
  }

  return 'status-pill status-restricted'
}
</script>

<template>
  <div class="user-page">
    <ViewHeader title="Users" :options="[]" :routes="[]" />

    <section class="summary-grid">
      <article v-for="card in summaryCards" :key="card.label" class="summary-card">
        <div class="summary-icon" :style="{ '--accent': card.accent }">
          <component :is="card.icon" :size="18" aria-hidden="true" />
        </div>

        <div class="summary-body">
          <p>{{ card.label }}</p>
          <strong>{{ card.value }}</strong>
          <span>{{ card.description }}</span>
        </div>
      </article>
    </section>

    <section class="top-grid">
      <article class="panel panel-profile">
        <div class="panel-heading">
          <div>
            <p class="panel-kicker">Organization Profile</p>
            <h3>Current workspace configuration</h3>
          </div>
          <UserRound :size="18" aria-hidden="true" class="heading-icon" />
        </div>

        <div class="profile-grid">
          <div class="profile-field">
            <span>Organisation name</span>
            <strong>{{ organization.name }}</strong>
          </div>
          <div class="profile-field">
            <span>Org number</span>
            <strong>{{ organization.orgNumber }}</strong>
          </div>
          <div class="profile-field profile-field-wide">
            <span>Headquarters</span>
            <strong>{{ organization.headquarters }}</strong>
          </div>
          <div class="profile-field">
            <span>Coverage</span>
            <strong>{{ organization.coverage }}</strong>
          </div>
        </div>
      </article>

      <article class="panel panel-policy">
        <div class="panel-heading">
          <div>
            <p class="panel-kicker">Permission Controls</p>
            <h3>Access policies</h3>
          </div>
          <ShieldCheck :size="18" aria-hidden="true" class="heading-icon" />
        </div>

        <div class="policy-list">
          <article v-for="policy in policies" :key="policy.label" class="policy-item">
            <div>
              <strong>{{ policy.label }}</strong>
              <p>{{ policy.description }}</p>
            </div>
            <span class="policy-state">{{ policy.state }}</span>
          </article>
        </div>
      </article>
    </section>

    <section class="content-grid">
      <article class="panel panel-table">
        <div class="panel-heading">
          <div>
            <p class="panel-kicker">Staff Roles & Permissions</p>
            <h3>All users in the organisation</h3>
          </div>
          <button type="button" class="invite-button">Invite New User</button>
        </div>

        <div class="table-head">
          <span>Staff member</span>
          <span>Assigned role</span>
          <span>Permissions</span>
          <span>Status</span>
        </div>

        <div class="table-body">
          <article v-for="user in users" :key="user.email" class="table-row">
            <div class="table-cell staff-cell">
              <span class="mobile-label">Staff member</span>
              <div class="avatar">{{ user.initials }}</div>
              <div>
                <strong>{{ user.name }}</strong>
                <p class="subtle-row">
                  <Mail :size="14" aria-hidden="true" />
                  {{ user.email }}
                </p>
              </div>
            </div>

            <div class="table-cell">
              <span class="mobile-label">Assigned role</span>
              <span class="role-chip">{{ user.role }}</span>
              <p class="subtle-row">
                <MapPin :size="14" aria-hidden="true" />
                {{ user.team }} · {{ user.location }}
              </p>
            </div>

            <div class="table-cell">
              <span class="mobile-label">Permissions</span>
              <div class="permission-list">
                <span v-for="permission in user.permissions" :key="permission" class="permission-tag">
                  {{ permission }}
                </span>
              </div>
              <p class="subtle-row">
                <Clock3 :size="14" aria-hidden="true" />
                Last active: {{ user.lastActive }}
              </p>
            </div>

            <div class="table-cell status-cell">
              <span class="mobile-label">Status</span>
              <span :class="statusClass(user.status)">{{ user.status }}</span>
            </div>
          </article>
        </div>
      </article>

      <article class="panel">
        <div class="panel-heading">
          <div>
            <p class="panel-kicker">Role Breakdown</p>
            <h3>Roles in use</h3>
          </div>
          <UserRound :size="18" aria-hidden="true" class="heading-icon" />
        </div>

        <div class="stack-list">
          <article v-for="role in roleBreakdown" :key="role.role" class="stack-item">
            <div>
              <strong>{{ role.role }}</strong>
              <p>Users assigned to this role</p>
            </div>
            <span class="count-pill">{{ role.count }}</span>
          </article>
        </div>
      </article>

      <article class="panel">
        <div class="panel-heading">
          <div>
            <p class="panel-kicker">Permission Usage</p>
            <h3>Most common permissions</h3>
          </div>
          <KeyRound :size="18" aria-hidden="true" class="heading-icon" />
        </div>

        <div class="stack-list">
          <article
            v-for="entry in permissionCoverage"
            :key="entry.permission"
            class="stack-item"
          >
            <div>
              <strong>{{ entry.permission }}</strong>
              <p>Users with this permission</p>
            </div>
            <span class="count-pill">{{ entry.count }}</span>
          </article>
        </div>
      </article>
    </section>
  </div>
</template>

<style scoped>
.user-page {
  color: var(--text);
}

.page-intro {
  margin-bottom: 1.5rem;
}

.eyebrow,
.panel-kicker,
.table-head span,
.mobile-label,
.profile-field span {
  text-transform: uppercase;
  letter-spacing: 0.08em;
  font-size: 0.72rem;
  font-weight: 700;
}

.eyebrow,
.panel-kicker {
  margin: 0 0 0.35rem;
  color: var(--primary);
}

.page-intro h2,
.panel-heading h3 {
  margin: 0;
}

.intro-copy,
.summary-body span,
.policy-item p,
.subtle-row,
.stack-item p,
.profile-field span {
  color: var(--text-secondary);
}

.intro-copy {
  max-width: 68ch;
  margin: 0.75rem 0 0;
  line-height: 1.6;
}

.summary-grid,
.top-grid,
.content-grid,
.policy-list,
.stack-list,
.table-body {
  display: grid;
  gap: 1rem;
}

.summary-grid {
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  margin-bottom: 1.5rem;
}

.top-grid {
  grid-template-columns: minmax(0, 1.3fr) minmax(320px, 0.9fr);
  margin-bottom: 1.5rem;
}

.content-grid {
  grid-template-columns: repeat(12, minmax(0, 1fr));
}

.panel,
.summary-card,
.table-row,
.stack-item,
.policy-item,
.profile-field {
  border: 1px solid var(--stroke);
  background: var(--surface);
  box-shadow: var(--shadow-soft);
}

.panel {
  padding: 1.5rem;
  border-radius: 24px;
}

.panel-profile,
.panel-table {
  grid-column: span 8;
}

.panel-policy {
  grid-column: span 4;
}

.content-grid > .panel:nth-child(2),
.content-grid > .panel:nth-child(3) {
  grid-column: span 4;
}

.panel-heading {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 1rem;
  margin-bottom: 1.25rem;
}

.heading-icon,
.summary-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.summary-card {
  display: flex;
  gap: 1rem;
  align-items: flex-start;
  padding: 1.25rem;
  border-radius: 20px;
}

.summary-icon {
  width: 42px;
  height: 42px;
  border-radius: 14px;
  background: var(--surface-muted);
  color: var(--accent);
}

.summary-body p,
.policy-item p,
.stack-item p {
  margin: 0;
}

.summary-body strong,
.policy-item strong,
.profile-field strong,
.table-cell strong,
.stack-item strong {
  display: block;
  margin: 0.18rem 0 0.35rem;
  font-size: 1rem;
}

.profile-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 1rem;
}

.profile-field {
  padding: 1rem;
  border-radius: 16px;
  background: var(--surface-muted);
}

.profile-field-wide {
  grid-column: span 2;
}

.policy-item {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 1rem;
  padding: 1rem;
  border-radius: 16px;
  background: var(--surface-muted);
}

.policy-state,
.count-pill,
.status-pill,
.role-chip {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 999px;
  font-size: 0.78rem;
  font-weight: 700;
}

.policy-state,
.count-pill {
  min-width: 64px;
  min-height: 34px;
  padding: 0 0.8rem;
  background: rgba(0, 102, 255, 0.08);
  color: var(--primary);
}

.invite-button {
  min-width: 152px;
}

.table-head,
.table-row {
  display: grid;
  grid-template-columns: minmax(220px, 1.4fr) minmax(170px, 1fr) minmax(260px, 1.7fr) 110px;
  gap: 1rem;
  align-items: center;
}

.table-head {
  padding: 0 0.5rem 0.75rem;
  border-bottom: 1px solid var(--stroke);
  color: var(--text-secondary);
}

.table-row {
  padding: 1rem;
  border-radius: 18px;
}

.table-cell {
  min-width: 0;
}

.staff-cell {
  display: flex;
  align-items: center;
  gap: 0.9rem;
}

.avatar {
  width: 52px;
  min-width: 52px;
  height: 52px;
  border-radius: 16px;
  display: grid;
  place-items: center;
  background: linear-gradient(135deg, var(--primary), #5a8cff);
  color: var(--bg);
  font-weight: 700;
  letter-spacing: 0.08em;
}

.role-chip {
  min-height: 30px;
  padding: 0 0.8rem;
  background: rgba(26, 28, 30, 0.08);
  color: var(--neutral);
}

.subtle-row {
  display: flex;
  align-items: center;
  gap: 0.4rem;
  margin: 0.45rem 0 0;
  line-height: 1.5;
}

.permission-list {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.permission-tag {
  display: inline-flex;
  align-items: center;
  min-height: 30px;
  padding: 0 0.75rem;
  border-radius: 999px;
  background: var(--surface-muted);
  border: 1px solid var(--stroke);
  font-size: 0.8rem;
  font-weight: 600;
}

.status-cell {
  display: flex;
  justify-content: flex-end;
}

.status-pill {
  min-width: 92px;
  min-height: 34px;
  padding: 0 0.85rem;
}

.status-active {
  background: rgba(0, 213, 110, 0.14);
  color: #0a8a4d;
}

.status-pending {
  background: rgba(255, 145, 0, 0.12);
  color: #b26700;
}

.status-restricted {
  background: rgba(26, 28, 30, 0.08);
  color: var(--text-secondary);
}

.stack-list {
  gap: 0.85rem;
}

.stack-item {
  display: flex;
  justify-content: space-between;
  gap: 1rem;
  align-items: center;
  padding: 1rem;
  border-radius: 18px;
}

.mobile-label {
  display: none;
  margin-bottom: 0.45rem;
  color: var(--text-secondary);
}

@media (max-width: 1200px) {
  .top-grid {
    grid-template-columns: 1fr;
  }

  .panel-profile,
  .panel-policy,
  .panel-table,
  .content-grid > .panel:nth-child(2),
  .content-grid > .panel:nth-child(3) {
    grid-column: span 12;
  }
}

@media (max-width: 860px) {
  .panel,
  .summary-card {
    padding: 1.2rem;
    border-radius: 20px;
  }

  .table-head {
    display: none;
  }

  .table-row {
    grid-template-columns: 1fr;
  }

  .table-cell,
  .status-cell {
    display: block;
  }

  .mobile-label {
    display: block;
  }

  .status-cell {
    justify-content: flex-start;
  }
}

@media (max-width: 640px) {
  .profile-grid {
    grid-template-columns: 1fr;
  }

  .profile-field-wide {
    grid-column: span 1;
  }

  .panel-heading,
  .policy-item,
  .staff-cell {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
