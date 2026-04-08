import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import * as api from '@/services/deviation'
import type { Deviation } from '@/interfaces/Deviation.interface'

export const useDeviationStore = defineStore('deviation', () => {
  const deviations = ref<Deviation[]>([])
  const loading = ref(false)
  const error = ref<string | null>(null)

  const filters = ref({
    status: 'ALL',
    severity: 'ALL',
  })

  const form = ref<Deviation>({
    title: '',
    category: 'TEMPERATURE',
    severity: 'CRITICAL',
    module: 'IK_FOOD',
    status: 'OPEN',
    description: '',
  })

  const filtered = computed(() => {
    if (!Array.isArray(deviations.value)) {
      return []
    }

    return deviations.value.filter((d) => {
      return (
        (filters.value.status === 'ALL' || d.status === filters.value.status) &&
        (filters.value.severity === 'ALL' || d.severity === filters.value.severity)
      )
    })
  })

  async function fetchDeviations() {
    loading.value = true
    error.value = null

    try {
      const list = await api.getDeviations()
      deviations.value = Array.isArray(list) ? list : []
    } catch {
      deviations.value = []
      error.value = 'Unable to load deviations right now.'
    } finally {
      loading.value = false
    }
  }

  async function createDeviation() {
    error.value = null

    try {
      const created = await api.createDeviation(form.value)

      if (created) {
        deviations.value.unshift(created)
      }

      resetForm()
    } catch {
      error.value = 'Unable to create deviation right now.'
    }
  }

  function resetForm() {
    form.value = {
      title: '',
      category: 'TEMPERATURE',
      severity: 'LOW',
      module: 'IK_FOOD',
      status: 'OPEN',
      description: '',
    }
  }

  return {
    deviations,
    filtered,
    filters,
    form,
    loading,
    error,
    fetchDeviations,
    createDeviation,
  }
})
