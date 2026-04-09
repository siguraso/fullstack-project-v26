import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import * as api from '@/services/deviation'
import { getAuthSession } from '@/services/auth'
import type { CreateDeviationInput, Deviation, DeviationFormInput } from '@/interfaces/Deviation.interface'

function getTodayDate() {
  return new Date().toISOString().slice(0, 10)
}

function resolveDiscoveredByDefault() {
  if (typeof window === 'undefined') {
    return ''
  }

  const session = getAuthSession()
  const fullName = session?.fullName?.trim()

  if (fullName) {
    return fullName
  }

  return session?.email ?? ''
}

function createEmptyForm(): DeviationFormInput {
  return {
    title: '',
    category: 'TEMPERATURE',
    severity: 'LOW',
    module: 'IK_FOOD',
    status: 'OPEN',
    reportedDate: getTodayDate(),
    discoveredBy: resolveDiscoveredByDefault(),
    reportedTo: '',
    assignedTo: '',
    issueDescription: '',
    immediateAction: '',
    rootCause: '',
    correctiveAction: '',
    completionNotes: '',
  }
}

function buildCreateDeviationPayload(
  form: DeviationFormInput,
  links?: Pick<CreateDeviationInput, 'logId'>,
): CreateDeviationInput {
  return {
    title: form.title.trim(),
    category: form.category,
    severity: form.severity,
    module: form.module,
    status: form.status,
    reportedDate: form.reportedDate,
    discoveredBy: form.discoveredBy.trim(),
    reportedTo: form.reportedTo.trim(),
    assignedTo: form.assignedTo.trim(),
    issueDescription: form.issueDescription.trim(),
    immediateAction: form.immediateAction.trim(),
    rootCause: form.rootCause.trim(),
    correctiveAction: form.correctiveAction.trim(),
    completionNotes: form.completionNotes.trim(),
    logId: links?.logId,
  }
}

export const useDeviationStore = defineStore('deviation', () => {
  const deviations = ref<Deviation[]>([])
  const loading = ref(false)
  const submitting = ref(false)
  const error = ref<string | null>(null)

  const filters = ref({
    status: 'ALL',
    severity: 'ALL',
  })

  const form = ref<DeviationFormInput>(createEmptyForm())

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

  function validateForm() {
    error.value = null

    if (!form.value.title.trim()) {
      error.value = 'Deviation form title is required.'
      return false
    }

    if (!form.value.issueDescription.trim()) {
      error.value = 'Describe what went wrong before submitting the deviation.'
      return false
    }

    return true
  }

  function patchForm(values: Partial<DeviationFormInput>) {
    form.value = {
      ...form.value,
      ...values,
    }
  }

  async function createDeviation(links?: Pick<CreateDeviationInput, 'logId'>) {
    if (!validateForm()) {
      return null
    }

    submitting.value = true

    try {
      const created = await api.createDeviation(buildCreateDeviationPayload(form.value, links))
      deviations.value.unshift(created)

      resetForm()
      return created
    } catch {
      error.value = 'Unable to create deviation right now.'
      return null
    } finally {
      submitting.value = false
    }
  }

  function resetForm() {
    form.value = createEmptyForm()
  }

  return {
    deviations,
    filtered,
    filters,
    form,
    loading,
    submitting,
    error,
    validateForm,
    patchForm,
    resetForm,
    fetchDeviations,
    createDeviation,
  }
})
