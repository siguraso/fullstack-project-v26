import { beforeEach, describe, expect, it, vi } from 'vitest'
import { defineComponent } from 'vue'
import { mount } from '@vue/test-utils'
import CreateTemperatureLog from '@/views/temperature-logs/components/CreateTemperatureLog.vue'

vi.mock('@/services/temperatureLog', () => ({
  createTemperatureLog: vi.fn(),
}))

const deviationStoreState = vi.hoisted(() => ({
  store: {
    error: null as string | null,
    resetForm: vi.fn(),
    patchForm: vi.fn(),
    validateForm: vi.fn(() => true),
    createDeviation: vi.fn(async () => ({ id: 41 })),
  },
}))

vi.mock('@/stores/deviation', () => ({
  useDeviationStore: () => deviationStoreState.store,
}))

import { createTemperatureLog } from '@/services/temperatureLog'

const InfoCardStub = defineComponent({
  template: `
    <section data-test="info-card-stub">
      <slot />
    </section>
  `,
})

const DeviationFormStub = defineComponent({
  emits: ['submit'],
  template:
    '<button data-test="submit-deviation" @click="$emit(\'submit\')">submit deviation</button>',
})

const zones = [
  {
    id: 1,
    name: 'Fridge',
    lowerLimitCelsius: 0,
    upperLimitCelsius: 4,
    active: true,
  },
]

beforeEach(() => {
  vi.clearAllMocks()
  deviationStoreState.store.error = null
  deviationStoreState.store.validateForm.mockReturnValue(true)
  deviationStoreState.store.createDeviation.mockResolvedValue({ id: 41 })
})

describe('CreateTemperatureLog', () => {
  it('submits normal in-range log and emits created', async () => {
    vi.mocked(createTemperatureLog).mockResolvedValue({
      id: 12,
      temperatureZoneId: 1,
      temperatureCelsius: 3,
      timestamp: '2026-04-09T11:00:00Z',
    })

    const wrapper = mount(CreateTemperatureLog, {
      props: {
        temperatureZones: zones,
      },
      global: {
        stubs: {
          InfoCard: InfoCardStub,
          DeviationForm: DeviationFormStub,
        },
      },
    })

    await wrapper.get('select').setValue('1')
    await wrapper.get('input.temperature-input').setValue('3')
    await wrapper.findAll('input')[1]!.setValue('Routine check')
    await wrapper.get('button.create-btn').trigger('click')

    expect(createTemperatureLog).toHaveBeenCalledWith({
      temperatureZoneId: 1,
      temperatureCelsius: 3,
      note: 'Routine check',
    })
    expect(wrapper.emitted('created')).toHaveLength(1)
    expect(wrapper.text()).toContain('Temperature log created successfully.')
  })

  it('shows validation error when required fields are missing', async () => {
    const wrapper = mount(CreateTemperatureLog, {
      props: {
        temperatureZones: zones,
      },
      global: {
        stubs: {
          InfoCard: InfoCardStub,
          DeviationForm: DeviationFormStub,
        },
      },
    })

    await wrapper.get('button.create-btn').trigger('click')

    expect(console.warn).toHaveBeenCalledWith(
      '[frontend][create-temperature-log] temperature log submit skipped because required fields were missing',
      {
        selectedZoneId: null,
        temperature: null,
      },
    )
    expect(wrapper.text()).toContain('Please select a storage unit and enter a temperature.')
    expect(createTemperatureLog).not.toHaveBeenCalled()
  })

  it('opens deviation flow for out-of-range readings and completes combined submit', async () => {
    vi.mocked(createTemperatureLog).mockResolvedValue({
      id: 55,
      temperatureZoneId: 1,
      temperatureCelsius: 8,
      timestamp: '2026-04-09T12:00:00Z',
      deviationCreated: false,
    })

    const wrapper = mount(CreateTemperatureLog, {
      props: {
        temperatureZones: zones,
      },
      global: {
        stubs: {
          InfoCard: InfoCardStub,
          DeviationForm: DeviationFormStub,
        },
      },
    })

    await wrapper.get('select').setValue('1')
    await wrapper.get('input.temperature-input').setValue('8')
    await wrapper.get('button.create-btn').trigger('click')

    expect(deviationStoreState.store.resetForm).toHaveBeenCalled()
    expect(deviationStoreState.store.patchForm).toHaveBeenCalled()
    expect(wrapper.find('.overlay-backdrop').exists()).toBe(true)

    await wrapper.get('[data-test="submit-deviation"]').trigger('click')

    expect(deviationStoreState.store.validateForm).toHaveBeenCalled()
    expect(createTemperatureLog).toHaveBeenCalledWith({
      temperatureZoneId: 1,
      temperatureCelsius: 8,
      note: null,
    })
    expect(deviationStoreState.store.createDeviation).toHaveBeenCalledWith({ logId: 55 })

    expect(wrapper.emitted('created')?.[0]?.[0]).toMatchObject({
      id: 55,
      deviationCreated: true,
    })
    expect(wrapper.text()).toContain('Temperature log and deviation created successfully.')
  })
})
