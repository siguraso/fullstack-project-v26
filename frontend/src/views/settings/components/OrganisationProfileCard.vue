<script setup lang="ts">
import { Building2 } from '@lucide/vue'
import InfoCard from '@/components/ui/InfoCard.vue'

type TenantForm = {
  name: string
  orgNumber: string
  address: string
  city: string
  country: string
  contactEmail: string
  contactPhone: string
}

const props = defineProps<{
  tenantError: string
  tenantSuccess: string
  tenantForm: TenantForm
  tenantIsDirty: boolean
  isSavingTenant: boolean
  hasTenant: boolean
}>()

const emit = defineEmits<{
  save: []
  reset: []
}>()
</script>

<template>
  <InfoCard
    title="Organisation profile"
    :icon="Building2"
    icon-background-color="var(--icon-bg-green)"
    icon-color="var(--icon-stroke-green)"
    class="main-panel"
  >
    <p v-if="props.tenantError" class="message-banner error-banner">{{ props.tenantError }}</p>
    <p v-if="props.tenantSuccess" class="message-banner success-banner">
      {{ props.tenantSuccess }}
    </p>

    <form class="form-grid" @submit.prevent="emit('save')">
      <label class="field">
        <span>Organisation name</span>
        <input v-model="props.tenantForm.name" type="text" placeholder="The Nordic Kitchen" />
      </label>

      <label class="field">
        <span>Org number</span>
        <input v-model="props.tenantForm.orgNumber" type="text" placeholder="987654321" />
      </label>

      <label class="field field-wide">
        <span>Address</span>
        <input v-model="props.tenantForm.address" type="text" placeholder="Storgata 14" />
      </label>

      <label class="field">
        <span>City</span>
        <input v-model="props.tenantForm.city" type="text" placeholder="Oslo" />
      </label>

      <label class="field">
        <span>Country</span>
        <input v-model="props.tenantForm.country" type="text" placeholder="Norway" />
      </label>

      <label class="field">
        <span>Contact email</span>
        <input
          v-model="props.tenantForm.contactEmail"
          type="email"
          placeholder="admin@example.com"
        />
      </label>

      <label class="field">
        <span>Contact phone</span>
        <input v-model="props.tenantForm.contactPhone" type="text" placeholder="+47 00 00 00 00" />
      </label>

      <div class="form-actions tenant-form-actions field-wide">
        <button
          type="submit"
          :disabled="!props.tenantIsDirty || props.isSavingTenant || !props.hasTenant"
        >
          {{ props.isSavingTenant ? 'Saving...' : 'Save workspace' }}
        </button>
        <button
          type="button"
          class="secondary-button"
          :disabled="!props.tenantIsDirty || props.isSavingTenant || !props.hasTenant"
          @click="emit('reset')"
        >
          Reset changes
        </button>
      </div>
    </form>
  </InfoCard>
</template>

<style scoped>
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

.field span {
  margin: 0 0 8px;
  font-size: 12px;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: var(--text-secondary);
  font-weight: 700;
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

.secondary-button {
  background: #ffffff;
  color: var(--text);
  border: 1px solid var(--border);
}

.secondary-button:hover {
  background: #f3f4f6;
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

button:disabled {
  cursor: not-allowed;
  opacity: 0.7;
}

@media (max-width: 860px) {
  .form-grid {
    grid-template-columns: 1fr;
  }

  .tenant-form-actions {
    justify-content: flex-start;
  }
}
</style>
