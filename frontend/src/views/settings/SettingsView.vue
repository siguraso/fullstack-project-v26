<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import Card from '@/components/ui/Card.vue'
import OrganisationProfileCard from '@/views/settings/components/OrganisationProfileCard.vue'
import StaffRolesCard from '@/views/settings/components/StaffRolesCard.vue'
import {
  deactivateUser,
  getCurrentTenant,
  sendStaffInvite,
  getUser,
  getUsers,
  updateCurrentTenant,
  updateUser,
} from '@/services/settings'
import { useTenantStore } from '@/stores/tenant'
import { type Tenant, type TenantUpdatePayload } from '@/interfaces/Tenant.interface'
import { type User, type UserUpdatePayload } from '@/interfaces/User.interface'
import { createLogger } from '@/services/util/logger'

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
const logger = createLogger('settings-view')

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
  logger.info('tenant load started')
  tenantError.value = ''

  try {
    const payload = await getCurrentTenant()
    tenant.value = payload
    tenantStore.setTenant(payload)
    applyTenantToForm(payload)
    logger.info('tenant load succeeded', { tenantId: payload.id, tenantName: payload.name })
  } catch (error) {
    tenantError.value = toErrorMessage(error, 'Unable to load workspace details.')
    logger.error('tenant load failed', error)
  }
}

async function loadUsersSection(showRefreshingState = false) {
  logger.info('users load started', { showRefreshingState })
  usersError.value = ''

  if (showRefreshingState) {
    isRefreshingUsers.value = true
  }

  try {
    users.value = await getUsers()
    logger.info('users load succeeded', {
      showRefreshingState,
      userCount: users.value.length,
    })
  } catch (error) {
    usersError.value = toErrorMessage(error, 'Unable to load staff members.')
    logger.error('users load failed', error, { showRefreshingState })
  } finally {
    if (showRefreshingState) {
      isRefreshingUsers.value = false
    }
  }
}

async function bootstrapPage() {
  logger.info('settings bootstrap started')
  isBootstrapping.value = true
  tenantSuccess.value = ''
  staffSuccess.value = ''

  await Promise.all([loadTenant(), loadUsersSection()])

  isBootstrapping.value = false
  logger.info('settings bootstrap completed', {
    hasTenant: Boolean(tenant.value),
    userCount: users.value.length,
  })
}

async function sendInviteForTesting() {
  const email = inviteEmail.value.trim()
  if (!email) {
    inviteError.value = 'Enter an email address before sending an invitation.'
    logger.warn('invite send skipped because email was missing')
    return
  }

  isSendingInvite.value = true
  inviteError.value = ''
  staffSuccess.value = ''
  logger.info('invite send started', { emailDomain: email.split('@')[1] ?? null })

  try {
    await sendStaffInvite(email)
    staffSuccess.value = `Invitation sent to ${email}.`
    inviteEmail.value = ''
    isInvitePopupOpen.value = false
    logger.info('invite send succeeded', { emailDomain: email.split('@')[1] ?? null })
  } catch (error) {
    inviteError.value = toErrorMessage(error, 'Unable to send invitation email.')
    logger.error('invite send failed', error, { emailDomain: email.split('@')[1] ?? null })
  } finally {
    isSendingInvite.value = false
  }
}

function toggleInvitePopup() {
  isInvitePopupOpen.value = !isInvitePopupOpen.value
  logger.info('invite popup toggled', { isOpen: isInvitePopupOpen.value })

  if (!isInvitePopupOpen.value) {
    inviteError.value = ''
    inviteEmail.value = ''
  }
}

function resetTenantForm() {
  if (!tenant.value) {
    logger.warn('tenant reset skipped because tenant is not loaded')
    return
  }

  tenantSuccess.value = ''
  tenantError.value = ''
  applyTenantToForm(tenant.value)
  logger.info('tenant form reset')
}

async function saveTenant() {
  if (!tenant.value) {
    logger.warn('tenant save skipped because tenant is not loaded')
    return
  }

  isSavingTenant.value = true
  tenantError.value = ''
  tenantSuccess.value = ''
  logger.info('tenant save started')

  try {
    const updatedTenant = await updateCurrentTenant(buildTenantPayload())
    tenant.value = updatedTenant
    tenantStore.setTenant(updatedTenant)
    applyTenantToForm(updatedTenant)
    tenantSuccess.value = 'Workspace settings saved.'
    logger.info('tenant save succeeded', { tenantId: updatedTenant.id, tenantName: updatedTenant.name })
  } catch (error) {
    tenantError.value = toErrorMessage(error, 'Unable to save workspace details.')
    logger.error('tenant save failed', error)
  } finally {
    isSavingTenant.value = false
  }
}

function closeUserEditor() {
  isUserEditorOpen.value = false
  isUserEditorLoading.value = false
  userEditorError.value = ''
  selectedUserId.value = null
  logger.info('user editor closed')
}

async function openUserEditor(user: User) {
  logger.info('user editor opened', { userId: user.id, role: user.role, active: user.active })
  isUserEditorOpen.value = true
  isUserEditorLoading.value = true
  selectedUserId.value = user.id
  userEditorError.value = ''
  applyUserToForm(user)

  try {
    const payload = await getUser(user.id)
    replaceUserInList(payload)
    applyUserToForm(payload)
    logger.info('user detail refresh succeeded', { userId: user.id })
  } catch (error) {
    userEditorError.value = toErrorMessage(
      error,
      'Unable to refresh staff member details. Showing list data instead.',
    )
    logger.error('user detail refresh failed', error, { userId: user.id })
  } finally {
    isUserEditorLoading.value = false
  }
}

async function saveSelectedUser() {
  if (selectedUserId.value === null) {
    logger.warn('user save skipped because no user is selected')
    return
  }

  isSavingUser.value = true
  userEditorError.value = ''
  logger.info('user save started', { userId: selectedUserId.value, role: userForm.role })

  try {
    const updatedUser = await updateUser(selectedUserId.value, buildUserPayload())
    replaceUserInList(updatedUser)
    await loadUsersSection()
    staffSuccess.value = `${fullName(updatedUser)} updated.`
    closeUserEditor()
    logger.info('user save succeeded', { userId: updatedUser.id, role: updatedUser.role })
  } catch (error) {
    userEditorError.value = toErrorMessage(error, 'Unable to update staff member.')
    logger.error('user save failed', error, { userId: selectedUserId.value })
  } finally {
    isSavingUser.value = false
  }
}

async function deactivateSelectedUser(user: User) {
  const confirmed = window.confirm(`Deactivate ${fullName(user)}? They will lose active access.`)

  if (!confirmed) {
    logger.warn('user deactivation cancelled', { userId: user.id })
    return
  }

  deactivatingUserId.value = user.id
  usersError.value = ''
  userEditorError.value = ''
  staffSuccess.value = ''
  logger.info('user deactivation started', { userId: user.id })

  try {
    await deactivateUser(user.id)
    await loadUsersSection()
    staffSuccess.value = `${fullName(user)} deactivated.`

    if (selectedUserId.value === user.id) {
      closeUserEditor()
    }
    logger.info('user deactivation succeeded', { userId: user.id })
  } catch (error) {
    const message = toErrorMessage(error, 'Unable to deactivate staff member.')

    if (selectedUserId.value === user.id && isUserEditorOpen.value) {
      userEditorError.value = message
    } else {
      usersError.value = message
    }

    logger.error('user deactivation failed', error, { userId: user.id })
  } finally {
    deactivatingUserId.value = null
  }
}

onMounted(() => {
  logger.info('view mounted')
  void bootstrapPage()
})
</script>

<template>
  <div class="settings-view">
    <h1>Organisation Configuration</h1>

    <section class="content-grid">
      <OrganisationProfileCard
        :tenant-error="tenantError"
        :tenant-success="tenantSuccess"
        :tenant-form="tenantForm"
        :tenant-is-dirty="tenantIsDirty"
        :is-saving-tenant="isSavingTenant"
        :has-tenant="Boolean(tenant)"
        @save="saveTenant"
        @reset="resetTenantForm"
      />

      <StaffRolesCard
        v-model:search-query="searchQuery"
        v-model:role-filter="roleFilter"
        v-model:status-filter="statusFilter"
        v-model:invite-email="inviteEmail"
        :invite-error="inviteError"
        :role-options="roleOptions"
        :is-invite-popup-open="isInvitePopupOpen"
        :is-sending-invite="isSendingInvite"
        :users-error="usersError"
        :staff-success="staffSuccess"
        :filtered-users="filteredUsers"
        @toggle-invite-popup="toggleInvitePopup"
        @send-invite="sendInviteForTesting"
        @edit-user="openUserEditor"
      />
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
.field span {
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

button:disabled {
  cursor: not-allowed;
  opacity: 0.7;
}

@media (max-width: 1600px) {
  .content-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 860px) {
  .hero-card,
  .user-editor-header {
    flex-direction: column;
  }

  .form-grid {
    grid-template-columns: 1fr;
  }

  .modal-backdrop {
    padding: 12px;
  }

  .form-actions {
    flex-direction: column;
    align-items: stretch;
  }
}

@media (max-width: 640px) {
  .modal-backdrop {
    align-items: flex-start;
    overflow-y: auto;
  }

  .user-editor-card {
    max-height: calc(100vh - 24px);
    overflow: auto;
  }
}
</style>
