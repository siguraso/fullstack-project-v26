import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import { getCurrentTenant, type Tenant } from '@/services/settings'

export const useTenantStore = defineStore('tenant', () => {
  const tenant = ref<Tenant | null>(null)
  const loading = ref(false)
  const error = ref<string | null>(null)

  const tenantName = computed(() => tenant.value?.name ?? 'Workspace')

  async function fetchTenant(force = false) {
    if (loading.value) {
      return tenant.value
    }

    if (!force && tenant.value) {
      return tenant.value
    }

    loading.value = true
    error.value = null

    try {
      const payload = await getCurrentTenant()
      tenant.value = payload
      return payload
    } catch (caughtError) {
      error.value =
        caughtError instanceof Error && caughtError.message.trim().length > 0
          ? caughtError.message
          : 'Unable to load workspace details.'
      return null
    } finally {
      loading.value = false
    }
  }

  function setTenant(payload: Tenant | null) {
    tenant.value = payload
  }

  return {
    tenant,
    tenantName,
    loading,
    error,
    fetchTenant,
    setTenant,
  }
})
