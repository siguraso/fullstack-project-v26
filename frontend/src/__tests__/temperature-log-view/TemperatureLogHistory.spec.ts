import { describe, expect, it } from 'vitest'
import { defineComponent } from 'vue'
import { mount } from '@vue/test-utils'
import TemperatureLogHistory from '@/views/temperature-logs/components/TemperatureLogHistory.vue'
import type { TemperatureLog } from '@/interfaces/TemperatureLog.interface'
import type { TemperatureZone } from '@/interfaces/TemperatureZone.interface'

const InfoCardStub = defineComponent({
  template: `
    <section data-test="info-card-stub">
      <slot name="extra-header-content" />
      <slot />
    </section>
  `,
})

const zones: TemperatureZone[] = [
  {
    id: 1,
    name: 'Fridge',
    lowerLimitCelsius: 0,
    upperLimitCelsius: 4,
    active: true,
  },
  {
    id: 2,
    name: 'Freezer',
    lowerLimitCelsius: -20,
    upperLimitCelsius: -5,
    active: true,
  },
]

const logs: TemperatureLog[] = [
  {
    id: 1,
    temperatureZoneId: 1,
    temperatureCelsius: 3,
    timestamp: '2026-04-09T07:00:00Z',
    recordedByName: 'Alice',
  },
  {
    id: 2,
    temperatureZoneId: 1,
    temperatureCelsius: 8,
    timestamp: '2026-04-09T11:00:00Z',
    recordedByName: 'Bob',
  },
  {
    id: 3,
    temperatureZoneId: 2,
    temperatureCelsius: -10,
    timestamp: '2026-04-09T09:00:00Z',
    recordedByName: 'Carol',
  },
  {
    id: 4,
    temperatureZoneId: 1,
    temperatureCelsius: 2,
    timestamp: '2026-04-09T08:00:00Z',
    recordedByName: 'Dan',
  },
  {
    id: 5,
    temperatureZoneId: 2,
    temperatureCelsius: -11,
    timestamp: '2026-04-09T06:00:00Z',
    recordedByName: 'Eve',
  },
]

describe('TemperatureLogHistory', () => {
  it('renders empty state when no logs exist', () => {
    const wrapper = mount(TemperatureLogHistory, {
      props: {
        temperatureLogs: [],
        temperatureZones: zones,
      },
      global: {
        stubs: {
          InfoCard: InfoCardStub,
        },
      },
    })

    expect(wrapper.text()).toContain('No temperature logs recorded yet.')
  })

  it('sorts newest first, supports pagination, filter, and emits view-log', async () => {
    const wrapper = mount(TemperatureLogHistory, {
      props: {
        temperatureLogs: logs,
        temperatureZones: zones,
      },
      global: {
        stubs: {
          InfoCard: InfoCardStub,
        },
      },
    })

    const bodyRows = wrapper.findAll('tbody tr')
    expect(bodyRows).toHaveLength(4)
    expect(bodyRows[0]!.text()).toContain('8')

    await wrapper.findAll('button.page-button')[1]!.trigger('click')
    expect(wrapper.text()).toContain('Page 2 of 2')

    const select = wrapper.get('select')
    await select.setValue('Freezer')

    expect(wrapper.text()).toContain('Page 1 of 1')
    const filteredRows = wrapper.findAll('tbody tr')
    expect(filteredRows).toHaveLength(2)

    await wrapper.get('button.view-btn').trigger('click')
    expect(wrapper.emitted('view-log')).toHaveLength(1)
  })

  it('marks out-of-range readings as abnormal', () => {
    const wrapper = mount(TemperatureLogHistory, {
      props: {
        temperatureLogs: [logs[1]!],
        temperatureZones: zones,
      },
      global: {
        stubs: {
          InfoCard: InfoCardStub,
        },
      },
    })

    const row = wrapper.get('tbody tr')
    expect(row.classes()).toContain('abnormal-row')
    expect(wrapper.text()).toContain('Abnormal')
  })
})
