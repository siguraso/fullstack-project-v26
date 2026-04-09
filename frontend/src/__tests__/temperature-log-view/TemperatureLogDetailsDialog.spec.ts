import { describe, expect, it } from 'vitest'
import { defineComponent } from 'vue'
import { mount } from '@vue/test-utils'
import TemperatureLogDetailsDialog from '@/views/temperature-logs/components/TemperatureLogDetailsDialog.vue'
import type { TemperatureLog } from '@/interfaces/TemperatureLog.interface'

const InfoCardStub = defineComponent({
  template: `
    <section data-test="info-card-stub">
      <slot name="extra-header-content" />
      <slot />
    </section>
  `,
})

const baseLog: TemperatureLog = {
  id: 10,
  temperatureZoneId: 1,
  temperatureCelsius: 3,
  timestamp: '2026-04-09T10:10:00Z',
  note: null,
  recordedByName: undefined,
  recordedById: undefined,
}

describe('TemperatureLogDetailsDialog', () => {
  it('renders fallback values and emits close', async () => {
    const wrapper = mount(TemperatureLogDetailsDialog, {
      props: {
        log: baseLog,
        zones: [
          {
            id: 1,
            name: 'Fridge',
            lowerLimitCelsius: 0,
            upperLimitCelsius: 4,
            active: true,
          },
        ],
      },
      global: {
        stubs: {
          InfoCard: InfoCardStub,
        },
      },
    })

    expect(wrapper.text()).toContain('Fridge')
    expect(wrapper.text()).toContain('Not set')

    await wrapper.get('button.close-btn').trigger('click')

    expect(wrapper.emitted('close')).toHaveLength(1)
  })

  it('prefers temperatureZoneName from the log payload', () => {
    const wrapper = mount(TemperatureLogDetailsDialog, {
      props: {
        log: {
          ...baseLog,
          temperatureZoneName: 'Manual Name',
          note: 'Measured after restock',
          recordedByName: 'Bob',
          recordedById: 42,
        },
        zones: [
          {
            id: 1,
            name: 'Fridge',
            lowerLimitCelsius: 0,
            upperLimitCelsius: 4,
            active: true,
          },
        ],
      },
      global: {
        stubs: {
          InfoCard: InfoCardStub,
        },
      },
    })

    const text = wrapper.text()
    expect(text).toContain('Manual Name')
    expect(text).toContain('Measured after restock')
    expect(text).toContain('Bob')
    expect(text).toContain('42')
  })
})
