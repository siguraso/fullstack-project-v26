import { computed } from 'vue'
import { useTenantStore } from '@/stores/tenant'

export function useTenant() {
  const tenantStore = useTenantStore()

  return {
    ...tenantStore,
    tenantId: computed(() => tenantStore.tenant?.id ?? null),
  }
}
