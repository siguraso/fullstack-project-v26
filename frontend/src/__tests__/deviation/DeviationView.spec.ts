import { beforeEach, describe, expect, it, vi } from 'vitest'
import { defineComponent } from 'vue'
import { flushPromises, mount } from '@vue/test-utils'

import type { Deviation } from '../../interfaces/Deviation.interface'

const sampleDeviation: Deviation = {
  id: 12,
  title: 'Cold room outside range',
  category: 'TEMPERATURE',
  severity: 'HIGH',
  module: 'IK_FOOD',
  status: 'OPEN',
  reportedDate: '2026-04-08',
  discoveredBy: 'Sam',
  reportedTo: 'Alex',
  assignedTo: 'Jordan',
  issueDescription: 'Cold room measured above threshold.',
  immediateAction: 'Moved affected food.',
  rootCause: 'Door left open.',
  correctiveAction: 'Re-trained staff.',
  completionNotes: '',
  createdAt: '2026-04-08T09:00:00.000Z',
}

const storeState = vi.hoisted(() => ({
  error: '',
  fetchDeviations: vi.fn(),
}))

vi.mock('@/stores/deviation', () => ({
  useDeviationStore: () => ({
    get error() {
      return storeState.error
    },
    fetchDeviations: storeState.fetchDeviations,
  }),
}))

import DeviationView from '../../views/deviation/DeviationView.vue'

const DeviationFormStub = defineComponent({
  props: {
    title: {
      type: String,
      required: true,
    },
  },
  template: '<section data-test="deviation-form">{{ title }}</section>',
})

const DeviationTableStub = defineComponent({
  emits: ['view-requested'],
  template: `
    <section data-test="deviation-table">
      <button data-test="open-details" @click="$emit('view-requested', deviation)">Open details</button>
    </section>
  `,
  data() {
    return {
      deviation: sampleDeviation,
    }
  },
})

const DeviationDetailsDialogStub = defineComponent({
  props: {
    deviation: {
      type: Object,
      required: false,
      default: null,
    },
    isOpen: {
      type: Boolean,
      required: true,
    },
  },
  emits: ['close', 'resolved'],
  template: `
    <section data-test="deviation-dialog">
      {{ isOpen }}|{{ deviation ? deviation.title : 'none' }}
      <button data-test="close-dialog" @click="$emit('close')">Close</button>
      <button data-test="resolve-dialog" @click="$emit('resolved')">Resolve</button>
    </section>
  `,
})

function mountDeviationView() {
  return mount(DeviationView, {
    global: {
      stubs: {
        DeviationDetailsDialog: DeviationDetailsDialogStub,
        DeviationForm: DeviationFormStub,
        DeviationTable: DeviationTableStub,
      },
    },
  })
}

beforeEach(() => {
  storeState.error = ''
  storeState.fetchDeviations.mockReset()
  storeState.fetchDeviations.mockResolvedValue(undefined)
})

describe('DeviationView', () => {
  it('fetches deviations on mount and shows the store error banner', async () => {
    storeState.error = 'Unable to load deviations right now.'

    const wrapper = mountDeviationView()
    await flushPromises()

    expect(storeState.fetchDeviations).toHaveBeenCalledTimes(1)
    expect(wrapper.get('.error-banner').text()).toBe('Unable to load deviations right now.')
    expect(wrapper.get('[data-test="deviation-form"]').text()).toBe('Report Deviation')

    wrapper.unmount()
  })

  it('opens and closes the details dialog from table events', async () => {
    const wrapper = mountDeviationView()
    await flushPromises()

    expect(wrapper.get('[data-test="deviation-dialog"]').text()).toContain('false|none')

    await wrapper.get('[data-test="open-details"]').trigger('click')

    expect(wrapper.get('[data-test="deviation-dialog"]').text()).toContain(
      'true|Cold room outside range',
    )

    await wrapper.get('[data-test="close-dialog"]').trigger('click')

    expect(wrapper.get('[data-test="deviation-dialog"]').text()).toContain('false|none')

    wrapper.unmount()
  })

  it('reloads deviations and closes the dialog when a deviation is resolved', async () => {
    const wrapper = mountDeviationView()
    await flushPromises()

    await wrapper.get('[data-test="open-details"]').trigger('click')
    await wrapper.get('[data-test="resolve-dialog"]').trigger('click')
    await flushPromises()

    expect(storeState.fetchDeviations).toHaveBeenCalledTimes(2)
    expect(wrapper.get('[data-test="deviation-dialog"]').text()).toContain('false|none')

    wrapper.unmount()
  })
})
