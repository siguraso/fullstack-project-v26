import { describe, expect, it } from 'vitest'
import { defineComponent } from 'vue'
import { mount } from '@vue/test-utils'
import CreateTemperatureZone from '@/views/temperature-logs/components/CreateTemperatureZone.vue'

const CardStub = defineComponent({
  template: `
    <div data-test="card-stub">
      <slot name="card-header" />
      <slot name="card-content" />
    </div>
  `,
})

describe('CreateTemperatureZone', () => {
  it('emits close when cancel is clicked', async () => {
    const wrapper = mount(CreateTemperatureZone, {
      global: {
        stubs: {
          Card: CardStub,
        },
      },
    })

    await wrapper.get('button.close-button').trigger('click')

    expect(wrapper.emitted('close')).toHaveLength(1)
  })

  it('emits create with form values', async () => {
    const wrapper = mount(CreateTemperatureZone, {
      global: {
        stubs: {
          Card: CardStub,
        },
      },
    })

    const inputs = wrapper.findAll('input')

    await inputs[0]!.setValue('Freezer')
    await inputs[1]!.setValue(-18)
    await inputs[2]!.setValue(-5)
    await wrapper.get('button.save-btn').trigger('click')

    expect(wrapper.emitted('create')).toEqual([
      [
        {
          name: 'Freezer',
          lowerLimitCelsius: -18,
          upperLimitCelsius: -5,
          active: true,
        },
      ],
    ])
  })
})
