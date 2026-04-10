<script setup lang="ts">
import { computed, ref } from 'vue'
import { Pencil, UserRound } from '@lucide/vue'
import Avatar from '@/components/ui/Avatar.vue'
import InfoCard from '@/components/ui/InfoCard.vue'
import { type User } from '@/interfaces/User.interface'

const props = defineProps<{
  inviteError: string
  searchQuery: string
  roleFilter: string
  statusFilter: 'ALL' | 'ACTIVE' | 'INACTIVE'
  roleOptions: string[]
  isInvitePopupOpen: boolean
  inviteEmail: string
  isSendingInvite: boolean
  isGeneratingLink: boolean
  generatedLink: string
  usersError: string
  staffSuccess: string
  filteredUsers: User[]
}>()

const emit = defineEmits<{
  'update:searchQuery': [value: string]
  'update:roleFilter': [value: string]
  'update:statusFilter': [value: 'ALL' | 'ACTIVE' | 'INACTIVE']
  'update:inviteEmail': [value: string]
  toggleInvitePopup: []
  sendInvite: []
  generateLink: []
  editUser: [user: User]
}>()

const searchQueryModel = computed({
  get: () => props.searchQuery,
  set: (value: string) => emit('update:searchQuery', value),
})

const roleFilterModel = computed({
  get: () => props.roleFilter,
  set: (value: string) => emit('update:roleFilter', value),
})

const statusFilterModel = computed({
  get: () => props.statusFilter,
  set: (value: 'ALL' | 'ACTIVE' | 'INACTIVE') => emit('update:statusFilter', value),
})

const inviteEmailModel = computed({
  get: () => props.inviteEmail,
  set: (value: string) => emit('update:inviteEmail', value),
})

const inviteMode = ref<'email' | 'link'>('email')
const copied = ref(false)

function copyLink() {
  navigator.clipboard.writeText(props.generatedLink).then(() => {
    copied.value = true
    setTimeout(() => { copied.value = false }, 2000)
  })
}

function fullName(user: Pick<User, 'firstName' | 'lastName'>) {
  return `${user.firstName} ${user.lastName}`.trim()
}
</script>

<template>
  <InfoCard
    title="Staff roles & permissions"
    :icon="UserRound"
    icon-background-color="var(--icon-bg-purple)"
    icon-color="var(--icon-stroke-purple)"
    class="main-panel"
  >
    <p v-if="props.inviteError" class="message-banner error-banner">{{ props.inviteError }}</p>
    <div class="staff-toolbar">
      <label class="field toolbar-search">
        <span>Search staff</span>
        <input
          v-model="searchQueryModel"
          type="search"
          placeholder="Search by name, email, or username"
        />
      </label>

      <label class="field toolbar-filter">
        <span>Role</span>
        <select v-model="roleFilterModel">
          <option v-for="role in props.roleOptions" :key="role" :value="role">
            {{ role === 'ALL' ? 'All roles' : role }}
          </option>
        </select>
      </label>

      <label class="field toolbar-filter">
        <span>Status</span>
        <select v-model="statusFilterModel">
          <option value="ALL">All statuses</option>
          <option value="ACTIVE">Active</option>
          <option value="INACTIVE">Inactive</option>
        </select>
      </label>

      <div class="toolbar-invite">
        <button type="button" class="invite-button" @click="emit('toggleInvitePopup')">
          {{ props.isInvitePopupOpen ? 'Close' : 'Invite Staff' }}
        </button>

        <transition name="invite-popup">
          <div v-if="props.isInvitePopupOpen" class="invite-popup">
            <div class="invite-mode-tabs">
              <button
                type="button"
                :class="['mode-tab', { active: inviteMode === 'email' }]"
                @click="inviteMode = 'email'"
              >Send email</button>
              <button
                type="button"
                :class="['mode-tab', { active: inviteMode === 'link' }]"
                @click="inviteMode = 'link'"
              >Get link</button>
            </div>

            <label class="field invite-popup-field">
              <span>Email address</span>
              <input
                v-model="inviteEmailModel"
                type="email"
                placeholder="new.staff@example.com"
                @keyup.enter="inviteMode === 'email' ? emit('sendInvite') : emit('generateLink')"
              />
            </label>

            <template v-if="inviteMode === 'email'">
              <button
                type="button"
                class="primary-button"
                :disabled="props.isSendingInvite || props.inviteEmail.trim().length === 0"
                @click="emit('sendInvite')"
              >
                {{ props.isSendingInvite ? 'Sending...' : 'Send invite email' }}
              </button>
            </template>

            <template v-else>
              <button
                type="button"
                class="primary-button"
                :disabled="props.isGeneratingLink || props.inviteEmail.trim().length === 0"
                @click="emit('generateLink')"
              >
                {{ props.isGeneratingLink ? 'Generating...' : 'Generate link' }}
              </button>

              <div v-if="props.generatedLink" class="generated-link-box">
                <input type="text" :value="props.generatedLink" readonly class="link-input" />
                <button type="button" class="copy-button" @click="copyLink">
                  {{ copied ? 'Copied!' : 'Copy' }}
                </button>
              </div>
            </template>
          </div>
        </transition>
      </div>
    </div>
    <p v-if="props.usersError" class="message-banner error-banner">{{ props.usersError }}</p>
    <p v-if="props.staffSuccess" class="message-banner success-banner">{{ props.staffSuccess }}</p>

    <div class="staff-table">
      <div class="table-head">
        <span>Staff member</span>
        <span>Role</span>
        <span>Contact</span>
        <span>Status</span>
        <span>Actions</span>
      </div>

      <div v-if="props.filteredUsers.length > 0" class="table-body">
        <article v-for="user in props.filteredUsers" :key="user.id" class="table-row">
          <div class="table-cell staff-cell">
            <span class="mobile-label">Staff member</span>
            <Avatar :name="fullName(user)" />
            <div>
              <strong>{{ fullName(user) }}</strong>
            </div>
          </div>

          <div class="table-cell">
            <span class="mobile-label">Role</span>
            <span class="role-chip">{{ user.role }}</span>
          </div>

          <div class="table-cell">
            <span class="mobile-label">Contact</span>
            <p>{{ user.email }}</p>
            <p>{{ user.phone || 'No phone saved' }}</p>
          </div>

          <div class="table-cell">
            <span class="mobile-label">Status</span>
            <span class="status-pill" :class="user.active ? 'status-active' : 'status-inactive'">
              {{ user.active ? 'Active' : 'Inactive' }}
            </span>
          </div>

          <div class="table-cell actions-cell">
            <span class="mobile-label">Actions</span>
            <button
              type="button"
              class="secondary-button table-icon-button"
              aria-label="Edit staff member"
              title="Edit staff member"
              @click="emit('editUser', user)"
            >
              <Pencil :size="16" aria-hidden="true" />
            </button>
          </div>
        </article>
      </div>

      <div v-else class="empty-state">
        <p>No staff members match the current filters.</p>
      </div>
    </div>
  </InfoCard>
</template>

<style scoped>
.main-panel {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.invite-button {
  border: none;
  padding: 8px 16px;
  border-radius: 8px;
  height: 42px;
}

.field {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.field span,
.mobile-label,
.table-head span {
  margin: 0 0 8px;
  font-size: 12px;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: var(--text-secondary);
  font-weight: 700;
}

.message-banner {
  margin: 0;
  border-radius: 12px;
  padding: 12px 14px;
  font-size: 14px;
}

.error-banner {
  background: #fff0f0;
  color: var(--cta-red-text);
  border: 1px solid #f2b8b5;
}

.success-banner {
  background: #edf8ef;
  color: #166534;
  border: 1px solid #badcc1;
}

.staff-toolbar {
  display: grid;
  grid-template-columns: minmax(220px, 1fr) auto auto auto;
  gap: 12px;
  align-items: end;
  margin-top: 10px;
  margin-bottom: 10px;
}

.toolbar-invite {
  position: relative;
}

.toolbar-search {
  min-width: 180px;
}

.invite-popup {
  position: absolute;
  top: calc(100% + 8px);
  right: 0;
  width: min(320px, 78vw);
  padding: 14px;
  border: 1px solid rgba(76, 61, 165, 0.16);
  border-radius: 14px;
  background: #ffffff;
  box-shadow: 0 12px 24px rgba(15, 23, 42, 0.16);
  display: grid;
  gap: 10px;
  z-index: 15;
}

.invite-popup-field {
  gap: 6px;
}

.invite-mode-tabs {
  display: flex;
  gap: 4px;
  background: #f3f4f6;
  border-radius: 8px;
  padding: 3px;
}

.mode-tab {
  flex: 1;
  border: none;
  background: transparent;
  border-radius: 6px;
  padding: 5px 10px;
  font-size: 13px;
  font-weight: 500;
  color: var(--text-secondary);
  cursor: pointer;
  transition: background 0.15s, color 0.15s;
  height: auto;
}

.mode-tab.active {
  background: #ffffff;
  color: var(--text);
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.generated-link-box {
  display: flex;
  gap: 6px;
  align-items: center;
}

.link-input {
  flex: 1;
  font-size: 12px;
  color: var(--text-secondary);
  background: #f7f7f8;
  border: 1px solid var(--border);
  border-radius: 8px;
  padding: 6px 10px;
  min-width: 0;
}

.copy-button {
  flex-shrink: 0;
  border: 1px solid var(--border);
  background: #ffffff;
  color: var(--text);
  border-radius: 8px;
  padding: 6px 12px;
  font-size: 13px;
  height: auto;
}

:deep(.invite-popup-enter-active),
:deep(.invite-popup-leave-active) {
  transition:
    opacity 0.18s ease,
    transform 0.18s ease;
}

:deep(.invite-popup-enter-from),
:deep(.invite-popup-leave-to) {
  opacity: 0;
  transform: translateY(-6px) scale(0.98);
}

.staff-table {
  border: 1px solid var(--border);
  border-radius: 18px;
  overflow-x: auto;
  background: #ffffff;
  margin-top: 10px;
}

.table-head,
.table-row {
  display: grid;
  grid-template-columns:
    minmax(220px, 1.5fr) minmax(100px, 0.75fr) minmax(180px, 1fr) minmax(92px, 0.65fr)
    minmax(150px, 0.85fr);
  gap: 14px;
  align-items: center;
}

.table-head {
  padding: 14px 18px;
  background: #f7f7f8;
  border-bottom: 1px solid var(--border);
}

.table-body {
  display: flex;
  flex-direction: column;
}

.table-row {
  padding: 16px 18px;
  border-bottom: 1px solid var(--border);
}

.table-row:last-child {
  border-bottom: 0;
}

.table-cell {
  min-width: 0;
}

.table-cell strong {
  display: block;
  margin-bottom: 4px;
  overflow-wrap: anywhere;
}

.table-cell p,
.empty-state p {
  margin: 0;
  color: var(--text-secondary);
  overflow-wrap: anywhere;
}

.staff-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.role-chip,
.status-pill {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-height: 30px;
  padding: 0 12px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 700;
}

.role-chip {
  background: #f3f4f6;
  color: var(--text);
}

.status-active {
  background: #e8f5eb;
  color: #166534;
}

.status-inactive {
  background: #fbe7e6;
  color: #9f2d25;
}

.actions-cell {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.secondary-button {
  background: #ffffff;
  color: var(--text);
  border: 1px solid var(--border);
}

.secondary-button:hover {
  background: #f3f4f6;
}

.table-icon-button {
  width: 36px;
  height: 36px;
  padding: 0;
  display: inline-grid;
  place-items: center;
  border-radius: 10px;
}

.empty-state {
  padding: 24px 18px;
  text-align: center;
}

.mobile-label {
  display: none;
}

button:disabled {
  cursor: not-allowed;
  opacity: 0.7;
}

@media (max-width: 1600px) {
  .staff-toolbar {
    grid-template-columns: minmax(180px, 0.7fr) auto auto auto;
  }

  .table-head,
  .table-row {
    grid-template-columns:
      minmax(180px, 1.3fr) minmax(88px, 0.7fr) minmax(160px, 1fr) minmax(84px, 0.6fr)
      minmax(128px, 0.8fr);
    gap: 10px;
    padding-left: 14px;
    padding-right: 14px;
  }
}

@media (max-width: 860px) {
  .staff-toolbar {
    grid-template-columns: 1fr;
  }

  .toolbar-invite {
    justify-self: stretch;
  }

  .invite-popup {
    position: static;
    width: 100%;
  }

  .table-head {
    display: none;
  }

  .table-row {
    grid-template-columns: 1fr;
    gap: 12px;
  }

  .table-cell,
  .actions-cell {
    display: flex;
    flex-direction: column;
    align-items: flex-start;
  }

  .mobile-label {
    display: block;
  }

  .actions-cell {
    flex-direction: row;
    flex-wrap: wrap;
  }
}
</style>
