<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { Building2, Pencil, UserRound } from '@lucide/vue'
import Card from '@/components/ui/Card.vue'
import InfoCard from '@/components/ui/InfoCard.vue'
import {
  deactivateUser,
  getCurrentTenant,
  sendStaffInvite,
  getUser,
  getUsers,
  updateCurrentTenant,
  updateUser,
  type Tenant,
  type TenantUpdatePayload,
  type User,
  type UserUpdatePayload,
} from '@/services/settings'
import { useTenantStore } from '@/stores/tenant'
import Avatar from '@/components/ui/Avatar.vue'

type TenantForm = {
  name: string
  orgNumber: string
  address: string
  city: string
  country: string
  contactEmail: string
  contactPhone: string
}

type UserForm = {
  firstName: string
  lastName: string
  email: string
  phone: string
  role: string
}

const DEFAULT_ROLE_OPTIONS = ['ADMIN', 'MANAGER', 'STAFF']
const tenantStore = useTenantStore()

const tenant = ref<Tenant | null>(null)
const users = ref<User[]>([])

const isBootstrapping = ref(true)
const isRefreshingUsers = ref(false)
const isSavingTenant = ref(false)
const isSavingUser = ref(false)
const tenantError = ref('')
const usersError = ref('')
const tenantSuccess = ref('')
const staffSuccess = ref('')
const inviteError = ref('')

const searchQuery = ref('')
const roleFilter = ref('ALL')
const statusFilter = ref<'ALL' | 'ACTIVE' | 'INACTIVE'>('ALL')
const selectedUserId = ref<number | null>(null)
const isUserEditorOpen = ref(false)
const isUserEditorLoading = ref(false)
const userEditorError = ref('')
const deactivatingUserId = ref<number | null>(null)
const inviteEmail = ref('')
const isSendingInvite = ref(false)
const isInvitePopupOpen = ref(false)

const tenantForm = reactive<TenantForm>({
  name: '',
  orgNumber: '',
  address: '',
  city: '',
  country: '',
  contactEmail: '',
  contactPhone: '',
})

const userForm = reactive<UserForm>({
  firstName: '',
  lastName: '',
  email: '',
  phone: '',
  role: 'STAFF',
})

const initialTenantPayload = ref('')

const roleOptions = computed(() =>
  [
    'ALL',
    ...new Set([...DEFAULT_ROLE_OPTIONS, ...users.value.map((user) => user.role), userForm.role]),
  ]
    .filter(Boolean)
    .sort((left, right) => {
      if (left === 'ALL') {
        return -1
      }

      if (right === 'ALL') {
        return 1
      }

      return left.localeCompare(right)
    }),
)

const filteredUsers = computed(() => {
  const query = searchQuery.value.trim().toLowerCase()

  return users.value.filter((user) => {
    const matchesQuery =
      query.length === 0 ||
      `${user.firstName} ${user.lastName}`.toLowerCase().includes(query) ||
      user.email.toLowerCase().includes(query) ||
      user.username.toLowerCase().includes(query)

    const matchesRole = roleFilter.value === 'ALL' || user.role === roleFilter.value
    const matchesStatus =
      statusFilter.value === 'ALL' ||
      (statusFilter.value === 'ACTIVE' && user.active) ||
      (statusFilter.value === 'INACTIVE' && !user.active)

    return matchesQuery && matchesRole && matchesStatus
  })
})

const tenantIsDirty = computed(
  () =>
    initialTenantPayload.value.length > 0 && initialTenantPayload.value !== serializeTenantForm(),
)

const selectedUser = computed(
  () => users.value.find((user) => user.id === selectedUserId.value) ?? null,
)

const isSelectedUserBeingDeactivated = computed(
  () => selectedUser.value !== null && deactivatingUserId.value === selectedUser.value.id,
)

function toErrorMessage(error: unknown, fallback: string) {
  return error instanceof Error && error.message.trim().length > 0 ? error.message : fallback
}

function initialsForUser(user: Pick<User, 'firstName' | 'lastName'>) {
  const first = user.firstName?.trim().charAt(0) ?? ''
  const last = user.lastName?.trim().charAt(0) ?? ''

  return `${first}${last}`.toUpperCase() || 'NA'
}

function fullName(user: Pick<User, 'firstName' | 'lastName'>) {
  return `${user.firstName} ${user.lastName}`.trim()
}

function serializeTenantForm() {
  return JSON.stringify(buildTenantPayload())
}

function buildTenantPayload(): TenantUpdatePayload {
  return {
    name: tenantForm.name.trim(),
    orgNumber: tenantForm.orgNumber.trim(),
    address: tenantForm.address.trim(),
    city: tenantForm.city.trim(),
    country: tenantForm.country.trim(),
    contactEmail: tenantForm.contactEmail.trim(),
    contactPhone: tenantForm.contactPhone.trim(),
  }
}

function applyTenantToForm(payload: Tenant) {
  tenantForm.name = payload.name ?? ''
  tenantForm.orgNumber = payload.org_number ?? ''
  tenantForm.address = payload.address ?? ''
  tenantForm.city = payload.city ?? ''
  tenantForm.country = payload.country ?? ''
  tenantForm.contactEmail = payload.contact_email ?? ''
  tenantForm.contactPhone = payload.contact_phone ?? ''
  initialTenantPayload.value = serializeTenantForm()
}

function buildUserPayload(): UserUpdatePayload {
  return {
    firstName: userForm.firstName.trim(),
    lastName: userForm.lastName.trim(),
    email: userForm.email.trim(),
    phone: userForm.phone.trim(),
    role: userForm.role.trim(),
  }
}

function applyUserToForm(payload: User) {
  userForm.firstName = payload.firstName ?? ''
  userForm.lastName = payload.lastName ?? ''
  userForm.email = payload.email ?? ''
  userForm.phone = payload.phone ?? ''
  userForm.role = payload.role ?? 'STAFF'
}

function replaceUserInList(nextUser: User) {
  const index = users.value.findIndex((user) => user.id === nextUser.id)

  if (index === -1) {
    users.value = [nextUser, ...users.value]
    return
  }

  users.value = users.value.map((user) => (user.id === nextUser.id ? nextUser : user))
}

async function loadTenant() {
  tenantError.value = ''

  try {
    const payload = await getCurrentTenant()
    tenant.value = payload
    tenantStore.setTenant(payload)
    applyTenantToForm(payload)
  } catch (error) {
    tenantError.value = toErrorMessage(error, 'Unable to load workspace details.')
  }
}

async function loadUsersSection(showRefreshingState = false) {
  usersError.value = ''

  if (showRefreshingState) {
    isRefreshingUsers.value = true
  }

  try {
    users.value = await getUsers()
  } catch (error) {
    usersError.value = toErrorMessage(error, 'Unable to load staff members.')
  } finally {
    if (showRefreshingState) {
      isRefreshingUsers.value = false
    }
  }
}

async function bootstrapPage() {
  isBootstrapping.value = true
  tenantSuccess.value = ''
  staffSuccess.value = ''

  await Promise.all([loadTenant(), loadUsersSection()])

  isBootstrapping.value = false
}

async function sendInviteForTesting() {
  const email = inviteEmail.value.trim()
  if (!email) {
    inviteError.value = 'Enter an email address before sending an invitation.'
    return
  }

  isSendingInvite.value = true
  inviteError.value = ''
  staffSuccess.value = ''

  try {
    await sendStaffInvite(email)
    staffSuccess.value = `Invitation sent to ${email}.`
    inviteEmail.value = ''
    isInvitePopupOpen.value = false
  } catch (error) {
    inviteError.value = toErrorMessage(error, 'Unable to send invitation email.')
  } finally {
    isSendingInvite.value = false
  }
}

function toggleInvitePopup() {
  isInvitePopupOpen.value = !isInvitePopupOpen.value

  if (!isInvitePopupOpen.value) {
    inviteError.value = ''
    inviteEmail.value = ''
  }
}

function resetTenantForm() {
  if (!tenant.value) {
    return
  }

  tenantSuccess.value = ''
  tenantError.value = ''
  applyTenantToForm(tenant.value)
}

async function saveTenant() {
  if (!tenant.value) {
    return
  }

  isSavingTenant.value = true
  tenantError.value = ''
  tenantSuccess.value = ''

  try {
    const updatedTenant = await updateCurrentTenant(buildTenantPayload())
    tenant.value = updatedTenant
    tenantStore.setTenant(updatedTenant)
    applyTenantToForm(updatedTenant)
    tenantSuccess.value = 'Workspace settings saved.'
  } catch (error) {
    tenantError.value = toErrorMessage(error, 'Unable to save workspace details.')
  } finally {
    isSavingTenant.value = false
  }
}

function closeUserEditor() {
  isUserEditorOpen.value = false
  isUserEditorLoading.value = false
  userEditorError.value = ''
  selectedUserId.value = null
}

async function openUserEditor(user: User) {
  isUserEditorOpen.value = true
  isUserEditorLoading.value = true
  selectedUserId.value = user.id
  userEditorError.value = ''
  applyUserToForm(user)

  try {
    const payload = await getUser(user.id)
    replaceUserInList(payload)
    applyUserToForm(payload)
  } catch (error) {
    userEditorError.value = toErrorMessage(
      error,
      'Unable to refresh staff member details. Showing list data instead.',
    )
  } finally {
    isUserEditorLoading.value = false
  }
}

async function saveSelectedUser() {
  if (selectedUserId.value === null) {
    return
  }

  isSavingUser.value = true
  userEditorError.value = ''

  try {
    const updatedUser = await updateUser(selectedUserId.value, buildUserPayload())
    replaceUserInList(updatedUser)
    await loadUsersSection()
    staffSuccess.value = `${fullName(updatedUser)} updated.`
    closeUserEditor()
  } catch (error) {
    userEditorError.value = toErrorMessage(error, 'Unable to update staff member.')
  } finally {
    isSavingUser.value = false
  }
}

async function deactivateSelectedUser(user: User) {
  const confirmed = window.confirm(`Deactivate ${fullName(user)}? They will lose active access.`)

  if (!confirmed) {
    return
  }

  deactivatingUserId.value = user.id
  usersError.value = ''
  userEditorError.value = ''
  staffSuccess.value = ''

  try {
    await deactivateUser(user.id)
    await loadUsersSection()
    staffSuccess.value = `${fullName(user)} deactivated.`

    if (selectedUserId.value === user.id) {
      closeUserEditor()
    }
  } catch (error) {
    const message = toErrorMessage(error, 'Unable to deactivate staff member.')

    if (selectedUserId.value === user.id && isUserEditorOpen.value) {
      userEditorError.value = message
    } else {
      usersError.value = message
    }
  } finally {
    deactivatingUserId.value = null
  }
}

onMounted(() => {
  bootstrapPage()
})
</script>

<template>
  <div class="settings-view">
    <h1>Organisation Configuration</h1>

    <section class="content-grid">
      <InfoCard
        title="Organisation profile"
        :icon="Building2"
        icon-background-color="var(--icon-bg-green)"
        icon-color="var(--icon-stroke-green)"
        class="main-panel"
      >
        <p v-if="tenantError" class="message-banner error-banner">{{ tenantError }}</p>
        <p v-if="tenantSuccess" class="message-banner success-banner">{{ tenantSuccess }}</p>

        <form class="form-grid" @submit.prevent="saveTenant">
          <label class="field">
            <span>Organisation name</span>
            <input v-model="tenantForm.name" type="text" placeholder="The Nordic Kitchen" />
          </label>

          <label class="field">
            <span>Org number</span>
            <input v-model="tenantForm.orgNumber" type="text" placeholder="987654321" />
          </label>

          <label class="field field-wide">
            <span>Address</span>
            <input v-model="tenantForm.address" type="text" placeholder="Storgata 14" />
          </label>

          <label class="field">
            <span>City</span>
            <input v-model="tenantForm.city" type="text" placeholder="Oslo" />
          </label>

          <label class="field">
            <span>Country</span>
            <input v-model="tenantForm.country" type="text" placeholder="Norway" />
          </label>

          <label class="field">
            <span>Contact email</span>
            <input v-model="tenantForm.contactEmail" type="email" placeholder="admin@example.com" />
          </label>

          <label class="field">
            <span>Contact phone</span>
            <input v-model="tenantForm.contactPhone" type="text" placeholder="+47 00 00 00 00" />
          </label>

          <div class="form-actions tenant-form-actions field-wide">
            <button type="submit" :disabled="!tenantIsDirty || isSavingTenant || !tenant">
              {{ isSavingTenant ? 'Saving...' : 'Save workspace' }}
            </button>
            <button
              type="button"
              class="secondary-button"
              :disabled="!tenantIsDirty || isSavingTenant || !tenant"
              @click="resetTenantForm"
            >
              Reset changes
            </button>
          </div>
        </form>
      </InfoCard>

      <InfoCard
        title="Staff roles & permissions"
        :icon="UserRound"
        icon-background-color="var(--icon-bg-purple)"
        icon-color="var(--icon-stroke-purple)"
        class="main-panel"
      >
        <p v-if="inviteError" class="message-banner error-banner">{{ inviteError }}</p>
        <div class="staff-toolbar">
          <label class="field toolbar-search">
            <span>Search staff</span>
            <input
              v-model="searchQuery"
              type="search"
              placeholder="Search by name, email, or username"
            />
          </label>

          <label class="field toolbar-filter">
            <span>Role</span>
            <select v-model="roleFilter">
              <option v-for="role in roleOptions" :key="role" :value="role">
                {{ role === 'ALL' ? 'All roles' : role }}
              </option>
            </select>
          </label>

          <label class="field toolbar-filter">
            <span>Status</span>
            <select v-model="statusFilter">
              <option value="ALL">All statuses</option>
              <option value="ACTIVE">Active</option>
              <option value="INACTIVE">Inactive</option>
            </select>
          </label>

          <div class="toolbar-invite">
            <button type="button" class="invite-button" @click="toggleInvitePopup">
              {{ isInvitePopupOpen ? 'Close' : 'Invite Staff' }}
            </button>

            <transition name="invite-popup">
              <div v-if="isInvitePopupOpen" class="invite-popup">
                <label class="field invite-popup-field">
                  <span>Email address</span>
                  <input
                    v-model="inviteEmail"
                    type="email"
                    placeholder="new.staff@example.com"
                    @keyup.enter="sendInviteForTesting"
                  />
                </label>

                <button
                  type="button"
                  class="primary-button"
                  :disabled="isSendingInvite || inviteEmail.trim().length === 0"
                  @click="sendInviteForTesting"
                >
                  {{ isSendingInvite ? 'Sending...' : 'Send' }}
                </button>
              </div>
            </transition>
          </div>
        </div>
        <p v-if="usersError" class="message-banner error-banner">{{ usersError }}</p>
        <p v-if="staffSuccess" class="message-banner success-banner">{{ staffSuccess }}</p>

        <div class="staff-table">
          <div class="table-head">
            <span>Staff member</span>
            <span>Role</span>
            <span>Contact</span>
            <span>Status</span>
            <span>Actions</span>
          </div>

          <div v-if="filteredUsers.length > 0" class="table-body">
            <article v-for="user in filteredUsers" :key="user.id" class="table-row">
              <div class="table-cell staff-cell">
                <span class="mobile-label">Staff member</span>
                <Avatar :name="fullName(user)" />
                <div>
                  <strong>{{ fullName(user) }}</strong>
                  <p>{{ user.username }}</p>
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
                <span
                  class="status-pill"
                  :class="user.active ? 'status-active' : 'status-inactive'"
                >
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
                  @click="openUserEditor(user)"
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
    </section>

    <div v-if="isUserEditorOpen" class="modal-backdrop" @click.self="closeUserEditor">
      <Card :header-disabled="true" class="user-editor-card">
        <template #card-content>
          <div class="user-editor-header">
            <div>
              <p class="section-label">Staff editor</p>
              <h2>{{ selectedUser ? fullName(selectedUser) : 'Staff member' }}</h2>
            </div>
            <button type="button" class="icon-button" @click="closeUserEditor">Close</button>
          </div>

          <p v-if="userEditorError" class="message-banner error-banner">{{ userEditorError }}</p>

          <form class="form-grid" @submit.prevent="saveSelectedUser">
            <label class="field">
              <span>First name</span>
              <input v-model="userForm.firstName" type="text" :disabled="isUserEditorLoading" />
            </label>

            <label class="field">
              <span>Last name</span>
              <input v-model="userForm.lastName" type="text" :disabled="isUserEditorLoading" />
            </label>

            <label class="field field-wide">
              <span>Email</span>
              <input v-model="userForm.email" type="email" :disabled="isUserEditorLoading" />
            </label>

            <label class="field">
              <span>Phone</span>
              <input v-model="userForm.phone" type="text" :disabled="isUserEditorLoading" />
            </label>

            <label class="field">
              <span>Role</span>
              <select v-model="userForm.role" :disabled="isUserEditorLoading">
                <option
                  v-for="role in roleOptions.filter((option) => option !== 'ALL')"
                  :key="role"
                  :value="role"
                >
                  {{ role }}
                </option>
              </select>
            </label>

            <div class="form-actions field-wide">
              <button
                type="submit"
                :disabled="isSavingUser || isUserEditorLoading || isSelectedUserBeingDeactivated"
              >
                {{ isSavingUser ? 'Saving...' : 'Save user' }}
              </button>
              <button type="button" class="secondary-button" @click="closeUserEditor">
                Cancel
              </button>
              <button
                v-if="selectedUser"
                type="button"
                class="danger-button"
                :disabled="
                  isSavingUser ||
                  isUserEditorLoading ||
                  isSelectedUserBeingDeactivated ||
                  !selectedUser.active
                "
                @click="deactivateSelectedUser(selectedUser)"
              >
                {{
                  isSelectedUserBeingDeactivated
                    ? 'Deactivating...'
                    : selectedUser.active
                      ? 'Deactivate user'
                      : 'Inactive'
                }}
              </button>
            </div>
          </form>
        </template>
      </Card>
    </div>
  </div>
</template>

<style scoped>
.settings-view {
  display: flex;
  flex-direction: column;
  gap: 24px;
  color: var(--text);
}

.hero-card {
  border: 1px solid rgba(15, 23, 42, 0.08);
  border-radius: 24px;
  padding: 28px;
  background: linear-gradient(135deg, #eef0e7 0%, #dde5d5 100%);
  display: block;
}

.hero-copy h1 {
  margin: 0 0 10px;
  font-size: 2rem;
  line-height: 1;
}

.hero-copy p:last-child {
  margin: 0;
  max-width: 60ch;
  color: var(--text-secondary);
  line-height: 1.55;
}

.section-label,
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

.content-grid {
  display: grid;
  grid-template-columns: minmax(0, 0.95fr) minmax(0, 1.25fr);
  gap: 16px;
}

.panel-intro p,
.table-cell p,
.empty-state p {
  margin: 0;
  color: var(--text-secondary);
}

.main-panel {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
}

.field {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.field-wide {
  grid-column: 1 / -1;
}

.form-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-start;
}

.tenant-form-actions {
  margin-top: 8px;
  justify-content: flex-end;
}

.tenant-form-actions button {
  min-width: 140px;
  padding: 10px 16px;
}

.secondary-button,
.icon-button {
  background: #ffffff;
  color: var(--text);
  border: 1px solid var(--border);
}

.secondary-button:hover,
.icon-button:hover {
  background: #f3f4f6;
}

.danger-button {
  background: var(--cta-red-btn);
  color: #ffffff;
}

.danger-button:hover {
  background: var(--cta-red-btn-hover);
}

.danger-button:disabled {
  background: #f0b4b4;
  cursor: not-allowed;
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

.invite-toolbar-button:hover {
  background: linear-gradient(180deg, #ffffff, #f0ecff);
  border-color: rgba(76, 61, 165, 0.36);
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

.table-cell p {
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

.modal-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(15, 23, 42, 0.28);
  display: grid;
  place-items: center;
  padding: 24px;
  z-index: 30;
}

.user-editor-card {
  width: min(680px, 100%);
  box-shadow:
    0 24px 64px rgba(15, 23, 42, 0.14),
    0 12px 28px rgba(15, 23, 42, 0.12);
}

.user-editor-header {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: flex-start;
  margin-bottom: 16px;
}

.user-editor-header h2 {
  margin: 0;
}

.icon-button {
  min-width: 72px;
}

.mobile-label {
  display: none;
}

button:disabled {
  cursor: not-allowed;
  opacity: 0.7;
}

@media (max-width: 1600px) {
  .content-grid {
    grid-template-columns: 1fr;
  }

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
  .hero-card,
  .user-editor-header {
    flex-direction: column;
  }

  .form-grid,
  .staff-toolbar {
    grid-template-columns: 1fr;
  }

  .toolbar-invite {
    justify-self: stretch;
  }

  .tenant-form-actions {
    justify-content: flex-start;
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

  .modal-backdrop {
    padding: 12px;
  }
}
</style>
