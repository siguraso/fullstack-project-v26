import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import * as api from '@/services/deviation'
import type { CreateDeviationInput, Deviation, DeviationFormInput } from '@/interfaces/Deviation.interface'

function getTodayDate() {
  return new Date().toISOString().slice(0, 10)
}

function createEmptyForm(): DeviationFormInput {
  return {
    title: '',
    referenceNumber: '',
    category: 'TEMPERATURE',
    severity: 'LOW',
    module: 'IK_FOOD',
    status: 'OPEN',
    reportedDate: getTodayDate(),
    discoveredBy: '',
    reportedTo: '',
    assignedTo: '',
    issueDescription: '',
    immediateAction: '',
    rootCause: '',
    correctiveAction: '',
    completionNotes: '',
  }
}

function buildSection(label: string, value: string) {
  const normalized = value.trim()
  return normalized ? `${label}: ${normalized}` : null
}

function buildDeviationDescription(form: DeviationFormInput) {
  return [
    buildSection('Reference number', form.referenceNumber),
    buildSection('Reported date', form.reportedDate),
    buildSection('Discovered by', form.discoveredBy),
    buildSection('Reported to', form.reportedTo),
    buildSection('Assigned to', form.assignedTo),
    buildSection('Describe the error / what went wrong', form.issueDescription),
    buildSection('Immediate action taken', form.immediateAction),
    buildSection('Possible cause', form.rootCause),
    buildSection('Corrective action / prevention', form.correctiveAction),
    buildSection('Corrective action completed', form.completionNotes),
  ]
    .filter((section): section is string => section !== null)
    .join('\n\n')
}

function buildCreateDeviationPayload(
  form: DeviationFormInput,
  links?: Pick<CreateDeviationInput, 'checklistItemId' | 'logId'>,
): CreateDeviationInput {
  return {
    title: form.title.trim(),
    category: form.category,
    severity: form.severity,
    module: form.module,
    status: form.status,
    description: buildDeviationDescription(form),
    checklistItemId: links?.checklistItemId,
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

  async function createDeviation(links?: Pick<CreateDeviationInput, 'checklistItemId' | 'logId'>) {
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
